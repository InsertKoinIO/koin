---
title: Starting Koin
---

# Starting Koin

This guide covers how to initialize the Koin container and configure it for your application.

## The `startKoin` Function

`startKoin` is the main entry point to launch Koin. It registers the container in `GlobalContext`, making it accessible throughout your application.

```kotlin
startKoin {
    modules(appModule)
}
```

Once started, dependencies are ready for resolution via `get()` or `by inject()`.

### Configuration Options

```kotlin
startKoin {
    // Logging
    logger(Level.INFO)

    // Properties
    environmentProperties()
    fileProperties()
    properties(mapOf("env" to "production"))

    // Modules
    modules(
        coreModule,
        networkModule,
        dataModule
    )

    // Lazy modules (background loading)
    lazyModules(analyticsModule, reportingModule)

    // Create eager singletons
    createEagerInstances()

    // Override control
    allowOverride(false)
}
```

| Option | Description |
|--------|-------------|
| `logger()` | Set logging level and implementation |
| `modules()` | Load modules immediately |
| `lazyModules()` | Load modules in background |
| `properties()` | Load properties from map |
| `fileProperties()` | Load from koin.properties file |
| `environmentProperties()` | Load from system/environment |
| `createEagerInstances()` | Create all `createdAtStart` singletons |
| `allowOverride()` | Enable/disable definition overriding |

:::info
`startKoin` can only be called **once**. To load additional modules later, use `loadKoinModules()`.
:::

## Starting the Koin Container

| Method | Use Case |
|--------|----------|
| `startKoin { }` | Standard apps - registers in GlobalContext |
| `koinApplication { }` | Testing, SDKs, isolated contexts - local instance |
| `koinConfiguration { }` | Configuration holder for dedicated APIs (Compose, Ktor) |

:::tip
With the **Koin Compiler Plugin**, typed variants are available: `startKoin<T>()`, `koinApplication<T>()`, `koinConfiguration<T>()`. See [Starting Koin with Compiler Plugin](#starting-koin-with-compiler-plugin) below.
:::

### `startKoin` - Global Instance

Most common approach - starts Koin globally:

```kotlin
fun main() {
    startKoin {
        modules(appModule)
    }

    // Use anywhere
    val service: MyService = get()
}
```

### `koinApplication` - Isolated Instance

Creates an isolated Koin instance (not in GlobalContext):

```kotlin
val myKoin = koinApplication {
    modules(myModule)
}.koin

// Use the isolated instance
val service: MyService = myKoin.get()
```

**Use cases:**
- Testing with isolated contexts
- SDK development (avoid polluting host app)
- Multiple Koin instances

### `koinConfiguration` - Configuration Holder

Creates a configuration to be used by dedicated APIs (Compose `KoinApplication`, Ktor plugin):

```kotlin
val config = koinConfiguration {
    modules(appModule)
}

// Used by Compose KoinApplication, Ktor, etc.
```

## Starting Koin with Compiler Plugin

When using the **Koin Compiler Plugin** with annotations, you can use **typed APIs** to start Koin - no generated code needed.

:::info
This requires the [Koin Compiler Plugin](/docs/setup/compiler-plugin). Your application class must be annotated with `@KoinApplication`.
:::

### Define Your Application

```kotlin
@Module
@Configuration
@ComponentScan("com.myapp")
class MyModule

@KoinApplication
class MyApp
```

### Typed Startup APIs

| API | Description |
|-----|-------------|
| `startKoin<T>()` | Start Koin globally with application T |
| `startKoin<T> { }` | Start with application T and additional configuration |
| `koinApplication<T>()` | Create isolated KoinApplication with T |
| `koinConfiguration<T>()` | Create KoinConfiguration from T (for Compose, Ktor) |

Where `T` is a class annotated with `@KoinApplication`.

### Examples

```kotlin
// Simple startup
startKoin<MyApp>()

// With additional configuration
startKoin<MyApp> {
    printLogger()
}

// Isolated instance
val myKoin = koinApplication<MyApp>().koin

// Configuration for Compose/Ktor
val config = koinConfiguration<MyApp>()
```

### Multi-Module Projects

```kotlin
// feature/src/main/kotlin/FeatureModule.kt
@Module
@Configuration
@ComponentScan("com.myapp.feature")
class FeatureModule

// app/src/main/kotlin/MyApp.kt
@KoinApplication
class MyApp

// Start Koin
startKoin<MyApp>()
```

## Platform Integrations

### Android

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
```

**With Compiler Plugin:**

```kotlin
@KoinApplication
class MyApp

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin<MyApp> {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}
```

### Compose

Use `KoinApplication` composable with `koinConfiguration`:

```kotlin
@Composable
fun App() {
    KoinApplication(
        configuration = koinConfiguration { modules(appModule) }
    ) {
        MainScreen()
    }
}
```

**With Compiler Plugin:**

```kotlin
@KoinApplication
class MyApp

@Composable
fun App() {
    KoinApplication(
        configuration = koinConfiguration<MyApp>()
    ) {
        MainScreen()
    }
}
```

### Ktor

```kotlin
fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
```

**With Compiler Plugin:**

```kotlin
@KoinApplication
class MyApp

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        withConfiguration<MyApp>()
    }
}
```

:::info
See [Ktor Integration](/docs/reference/koin-ktor/ktor) for more details.
:::

### Kotlin Multiplatform

Share configuration across platforms:

```kotlin
// commonMain
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule)
    }
}

// androidMain
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
            androidLogger()
        }
    }
}

// iosMain
fun initKoinIos() = initKoin()
```

## Dynamic Module Management

### Loading Modules After Startup

```kotlin
// Initial startup
startKoin {
    modules(coreModule)
}

// Later, load additional modules
loadKoinModules(featureModule)
```

### Unloading Modules

```kotlin
unloadKoinModules(featureModule)
```

### Feature Toggle Example

```kotlin
if (isFeatureEnabled) {
    loadKoinModules(premiumFeatureModule)
}

// Later, if disabled
unloadKoinModules(premiumFeatureModule)
```

## Stopping Koin

Close the container and release resources:

```kotlin
stopKoin()
```

For isolated instances:

```kotlin
val koinApp = koinApplication { modules(myModule) }
koinApp.close()
```

### Using `use { }` for Safe Cleanup

Both `KoinApplication` and `Koin` implement `AutoCloseable`, so you can use Kotlin's `use { }` block for automatic cleanup. This is useful for testing, short-lived contexts, or any situation where you want to ensure resources are released:

```kotlin
koinApplication {
    modules(myModule)
}.use { app ->
    val service: MyService = app.koin.get()
    // work with service
}
// app.close() called automatically, even on exceptions
```

## Logging

### Enable Logging

```kotlin
startKoin {
    logger(Level.INFO)  // Or DEBUG, WARNING, ERROR, NONE
}
```

### Available Loggers

| Logger | Platform | Description |
|--------|----------|-------------|
| `EmptyLogger` | All | No logging (default) |
| `PrintLogger` | All | Console output |
| `AndroidLogger` | Android | Android Logcat |
| `SLF4JLogger` | JVM | SLF4J integration |

### Platform-Specific Loggers

```kotlin
// Android
startKoin {
    androidLogger(Level.DEBUG)
}

// Ktor
install(Koin) {
    slf4jLogger()
}
```

## Properties

### Loading Properties

```kotlin
startKoin {
    // From environment
    environmentProperties()

    // From file (koin.properties)
    fileProperties()

    // From code
    properties(mapOf(
        "server_url" to "https://api.example.com",
        "api_key" to "secret123"
    ))
}
```

### Using Properties

```kotlin
val appModule = module {
    single {
        ApiClient(
            url = getProperty("server_url"),
            key = getProperty("api_key", "default")
        )
    }
}
```

## Best Practices

1. **Call `startKoin` once** - At application entry point
2. **Load critical modules immediately** - Use `modules()`
3. **Use lazy modules** - Defer non-critical with `lazyModules()`
4. **Enable logging in development** - `logger(Level.DEBUG)`
5. **Use strict mode in production** - `allowOverride(false)`
6. **Stop Koin between tests** - Call `stopKoin()` to reset state

## Next Steps

- **[Modules](/docs/reference/koin-core/modules)** - Organize your definitions
- **[Definitions](/docs/reference/koin-core/definitions)** - Create definitions with DSL or Annotations
- **[Injection](/docs/reference/koin-core/injection)** - Retrieve dependencies
