---
title: Start Koin Reference
---

Quick reference for starting Koin. For detailed guide see **[Core - Starting Koin](/docs/reference/koin-core/starting-koin)**.

## Starting Methods

| Method | Use Case |
|--------|----------|
| `startKoin { }` | Standard apps - registers in GlobalContext |
| `koinApplication { }` | Testing, SDKs - isolated instance |
| `koinConfiguration { }` | Configuration for Compose, Ktor |
| `startKoin<T>()` | Typed startup with Compiler Plugin |

## Basic Startup

```kotlin
startKoin {
    modules(appModule)
}
```

## Complete Configuration

```kotlin
startKoin {
    logger(Level.INFO)
    environmentProperties()
    fileProperties()
    properties(mapOf("env" to "production"))
    modules(coreModule, networkModule)
    lazyModules(analyticsModule)
    createEagerInstances()
    allowOverride(false)
}
```

## Configuration Options

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

## Typed Startup (Compiler Plugin)

Requires [Koin Compiler Plugin](/docs/setup/compiler-plugin) and `@KoinApplication`:

```kotlin
@KoinApplication
class MyApp

// Start
startKoin<MyApp>()

// With configuration
startKoin<MyApp> {
    printLogger()
}
```

## Dynamic Module Management

```kotlin
// Load after startup
loadKoinModules(featureModule)

// Unload
unloadKoinModules(featureModule)
```

## Stopping Koin

```kotlin
stopKoin()  // Global instance

// Isolated instance
koinApp.close()

// Safe cleanup with use { } (KoinApplication and Koin implement AutoCloseable)
koinApplication { modules(myModule) }.use { app ->
    val service: MyService = app.koin.get()
}
// app.close() called automatically
```

## Logging

| Logger | Platform | Description |
|--------|----------|-------------|
| `EmptyLogger` | All | No logging (default) |
| `PrintLogger` | All | Console output |
| `AndroidLogger` | Android | Logcat |
| `SLF4JLogger` | JVM | SLF4J |

```kotlin
startKoin {
    logger(Level.DEBUG)  // or androidLogger() for Android
}
```

## Properties

```kotlin
startKoin {
    environmentProperties()
    fileProperties()  // koin.properties
    properties(mapOf("key" to "value"))
}

// In module
single {
    ApiClient(url = getProperty("server_url"))
}
```

## Platform Examples

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

### Compose

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

### Ktor

```kotlin
fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
```

## See Also

- **[Core - Starting Koin](/docs/reference/koin-core/starting-koin)** - Complete guide
- **[Lazy Modules](/docs/reference/koin-core/lazy-modules)** - Background loading
- **[KoinComponent](/docs/reference/koin-core/koin-component)** - Retrieving instances
