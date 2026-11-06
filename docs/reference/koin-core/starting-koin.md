---
title: Starting Koin
description: Start and stop the Koin container with startKoin or startKoin<T>(), configure logging and properties, and load modules at runtime.
---

This page covers how to start the Koin container, the options you can pass at start, typed startup with `@KoinApplication`, logging, properties, and how to load or unload modules later. It assumes you know what a module is (see [Modules](/docs/reference/koin-core/modules)).

Code samples come in three tabs: Compiler Plugin DSL, Annotations, Constructor / Classic DSL. Pick yours, the choice applies to every page.

## Start Koin

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        printLogger()
        modules(appModule)
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [AppModule::class])
class MyApp

fun main() {
    startKoin<MyApp> {
        printLogger()
    }
}
```

The Koin Compiler Plugin replaces `startKoin<MyApp>()` with a `startKoin { }` call that loads the modules listed in `@KoinApplication`, plus the `@Configuration` modules it discovers. See [Typed startup with @KoinApplication](#typed-startup-with-koinapplication).

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        printLogger()
        modules(appModule)
    }
}
```

</TabItem>
</Tabs>

`startKoin` creates the container and registers it in `GlobalContext`, the default context for your process. Once it returns, every `get()`, `by inject()` or platform API (`by viewModel()`, `koinInject()`) resolves from this container.

You can call `startKoin` only once per process. A second call throws `KoinApplicationAlreadyStartedException`. To add modules after start, use [`loadKoinModules`](#load-and-unload-modules-at-runtime).

## Start options

Everything inside the `startKoin { }` block configures the container:

```kotlin
startKoin {
    printLogger(Level.DEBUG)
    properties(mapOf("server_url" to "https://api.example.com"))
    allowOverride(false)
    modules(coreModule, networkModule)
    lazyModules(analyticsModule)
}
```

| Option | What it does |
|--------|--------------|
| `modules(...)` | Loads modules immediately. Takes one module, several, or a `List<Module>` |
| `lazyModules(...)` | Loads modules in the background. Needs `koin-core-coroutines`, see [Lazy Modules](/docs/reference/koin-core/lazy-modules) |
| `printLogger(level)` | Logs to the console. See [Logging](#logging) |
| `logger(logger)` | Uses your own `Logger` implementation |
| `properties(map)` | Adds properties from a map. See [Properties](#properties) |
| `fileProperties(file)` | Loads properties from a classpath file, `/koin.properties` by default (JVM only) |
| `environmentProperties()` | Loads system properties and environment variables (JVM only) |
| `allowOverride(false)` | Fails when two definitions declare the same type without `.override()`. See [Overriding a definition](/docs/reference/koin-core/definitions#overriding-a-definition) |
| `strictOverride()` | Same as `allowOverride(false)` |
| `createEagerInstances()` | Creates the `createdAtStart` definitions now. `startKoin` and `koinApplication` already do it at the end of the block, so you need it only with `koinApplication(createEagerInstances = false)` |

The `startKoin` block runs on the calling thread. Every module you load there, and every `createdAtStart` definition, adds to startup time.

## Typed startup with @KoinApplication

With the [Koin Compiler Plugin](/docs/setup/compiler-plugin), an application class annotated with `@KoinApplication` describes which modules to load. You start Koin from that class with a typed call:

```kotlin
@Module
@ComponentScan("com.myapp.data")
class DataModule

@Module(includes = [DataModule::class])
class AppModule

@KoinApplication(modules = [AppModule::class])
class MyApp
```

| Call | Result |
|------|--------|
| `startKoin<MyApp>()` | Starts Koin in `GlobalContext` with the modules of `MyApp` |
| `startKoin<MyApp> { ... }` | Same, with a configuration block (logger, properties, extra DSL modules) |
| `koinApplication<MyApp>()` | Creates an isolated `KoinApplication`, not registered globally |
| `koinConfiguration<MyApp>()` | Creates a `KoinConfiguration`, for APIs that start Koin for you (Compose `KoinApplication`, Ktor) |
| `withConfiguration<MyApp>()` | Adds the modules of `MyApp` to an existing `KoinApplication`, for example inside Ktor's `install(Koin) { }` |

These functions are imported from `org.koin.plugin.module.dsl`. They are compiler stubs: without the Compiler Plugin applied, they throw at runtime with a message telling you the plugin is missing.

`@KoinApplication` takes two parameters:

- `modules`: the `@Module` classes to load explicitly.
- `configurations`: the configuration labels to discover. See [Configuration labels](#configuration-labels).

Each `@Module` class brings its `includes` and its `@ComponentScan` definitions. The configuration block of `startKoin<MyApp> { }` is a normal `startKoin` block, so you can mix annotated modules with DSL modules:

```kotlin
startKoin<MyApp> {
    printLogger()
    modules(legacyDslModule)
}
```

### Loading @Module classes without @KoinApplication

Inside a plain `startKoin { }` or `koinApplication { }` block, you can load `@Module` classes directly:

```kotlin
import org.koin.plugin.module.dsl.module
import org.koin.plugin.module.dsl.modules

startKoin {
    module<NetworkModule>()
    modules(DataModule::class, CacheModule::class)
}
```

This helps in tests, when you want only some modules. `module<T>()` and `modules(vararg KClass)` are compiler stubs too, and they don't work inside `startKoin<T> { }`, which the plugin already rewrites.

:::note
With the Compiler Plugin, there is no `AppModule().module` extension to call. Load annotated modules with `@KoinApplication` and `startKoin<T>()`, or with `module<T>()` as shown above.
:::

### Configuration labels

A `@Module` tagged with `@Configuration` is discovered automatically by `@KoinApplication`, including from other Gradle modules on the classpath (see [Auto-discovery with @Configuration](/docs/reference/koin-core/modules#auto-discovery-with-configuration)). Labels let you pick which of these modules to load:

```kotlin
@Module
@Configuration
class CoreModule

@Module
@Configuration("prod")
class ProdDatabaseModule

@Module
@Configuration("test")
class TestDatabaseModule

@Module
@Configuration("default", "test")
class LoggingModule
```

```kotlin
@KoinApplication
class MyApp // loads CoreModule and LoggingModule

@KoinApplication(configurations = ["default", "prod"])
class ProdApp // loads CoreModule, LoggingModule and ProdDatabaseModule

@KoinApplication(configurations = ["test"])
class TestApp // loads TestDatabaseModule and LoggingModule
```

The rules:

- `@Configuration` with no value is the `"default"` label. `@Configuration("default")` is the same.
- `@KoinApplication` with no `configurations` loads the `"default"` label only.
- When you list labels, only those labels load. Add `"default"` to the list if you still want the default modules.
- A module can carry several labels.

## Isolated instance with koinApplication

`koinApplication { }` creates a container that is not registered in `GlobalContext`:

```kotlin
val koinApp = koinApplication {
    modules(sdkModule)
}

val service = koinApp.koin.get<SdkService>()
```

Global APIs (`KoinComponent`, `by inject()` in an Activity) don't see this container. Use it for libraries and SDKs that must not share the host app's container, or for tests that need their own instance. The full pattern is in [Context Isolation](/docs/reference/koin-core/context-isolation).

`koinConfiguration { }` builds a configuration without starting anything. You pass it to APIs that start Koin themselves, such as Compose `KoinApplication` and the Ktor plugin.

## Starting on each platform

- Android: start Koin in `Application.onCreate()` with `androidContext()` and `androidLogger()`. See [Starting Koin on Android](/docs/reference/koin-android/start).
- Compose: start Koin from a composable with `KoinApplication` or `KoinMultiplatformApplication`. See [Koin for Compose](/docs/reference/koin-compose/compose).
- Ktor: install the `Koin` plugin in your `Application` module. See [Koin for Ktor](/docs/reference/koin-ktor/ktor).
- Kotlin Multiplatform: share an `initKoin()` function in `commonMain` and call it from each platform. See [KMP Setup](/docs/reference/koin-core/kmp-setup).

## Load and unload modules at runtime

After start, you can add and remove modules in the global container:

```kotlin
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

if (premiumEnabled) {
    loadKoinModules(premiumModule)
}

// later
unloadKoinModules(premiumModule)
```

Both functions take one module or a `List<Module>`. `unloadKoinModules` removes the definitions of the module and drops their instances, so a later `get()` for these types fails.

The Compiler Plugin can't verify a graph assembled at runtime: it can't know when, or if, the module is loaded. Types that other definitions need from a runtime-loaded module must be marked `@Provided`, and the plugin reports the module itself as not loaded at start. See [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety). If you load modules later only to speed up startup, [Lazy Modules](/docs/reference/koin-core/lazy-modules) is usually a better fit.

## Stop Koin

```kotlin
import org.koin.core.context.stopKoin

stopKoin()
```

`stopKoin()` closes the global container: it releases every instance, calls the `onClose` callbacks of your definitions, and clears `GlobalContext`. You can then call `startKoin` again. For an isolated container, call `close()` on the `KoinApplication`:

```kotlin
koinApp.close()
```

In tests, stop Koin after each test so the next one starts clean. [Testing](/docs/reference/koin-test/testing) shows the JUnit rules that do it for you.

## Logging

Koin logs nothing by default (`EmptyLogger`). Turn logging on in the start block:

```kotlin
startKoin {
    printLogger(Level.DEBUG)
    modules(appModule)
}
```

`Level` is `DEBUG`, `INFO`, `WARNING`, `ERROR` or `NONE`. The default level of each logger is `INFO`.

| Logger | Start option | Platform | Artifact |
|--------|--------------|----------|----------|
| `EmptyLogger` | (default) | All | `koin-core` |
| `PrintLogger` | `printLogger()` | All | `koin-core` |
| `AndroidLogger` | `androidLogger()` | Android (Logcat) | `koin-android` |
| `SLF4JLogger` | `slf4jLogger()` | JVM | `koin-logger-slf4j` |

At `DEBUG` level, Koin logs each module load and each resolution with its duration. This is useful to find a slow definition, and too verbose for production.

To send logs somewhere else, extend `Logger` and pass it with `logger()`:

```kotlin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE

class AppLogger : Logger(Level.INFO) {
    override fun display(level: Level, msg: MESSAGE) {
        MyLogSystem.log(level.name, msg)
    }
}

startKoin {
    logger(AppLogger())
}
```

## Properties

Properties are key-value pairs stored in the container. Use them for configuration values such as URLs or keys.

### Loading properties

```kotlin
startKoin {
    properties(mapOf("server_url" to "https://api.example.com"))
    fileProperties()          // JVM: reads /koin.properties from the classpath
    environmentProperties()   // JVM: system properties and environment variables
}
```

On Android, `androidFileProperties()` reads `koin.properties` from the `assets` folder. You can also set and read properties at runtime on the `Koin` instance:

```kotlin
val koin = KoinPlatform.getKoin()
koin.setProperty("session_token", token)
val token: String? = koin.getProperty("session_token")
```

Values loaded from a file or the environment are `String`. Convert them yourself (`getProperty<String>("timeout").toInt()`).

### Injecting properties

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

Annotate the constructor parameter with `@Property` (from `koin-annotations`):

```kotlin
class ApiClient(@Property("server_url") val url: String)

val networkModule = module {
    single<ApiClient>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class ApiClient(@Property("server_url") val url: String)
```

To give a property a default value, annotate a top-level value with `@PropertyValue` and the same key:

```kotlin
@PropertyValue("server_url")
val defaultServerUrl = "https://api.example.com"
```

The generated code reads `getProperty("server_url", defaultServerUrl)`, so `ApiClient` gets the default when nobody set the property.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val networkModule = module {
    single { ApiClient(url = getProperty("server_url")) }

    // with a default value
    single { ApiClient(url = getProperty("server_url", "https://api.example.com")) }
}
```

`getProperty(key)` throws if the property is missing. `getPropertyOrNull(key)` returns `null` instead.

</TabItem>
</Tabs>

Read properties in one configuration class and inject that class, instead of calling `getProperty` in many places. Keys then live in one file.

## Limits

- `startKoin` runs once per process. Call `stopKoin()` before starting again.
- `fileProperties()` and `environmentProperties()` exist on the JVM only. On other platforms, pass a map with `properties()`.
- Typed startup (`startKoin<T>()`, `module<T>()`) needs the Compiler Plugin. The KSP processor is deprecated, see [Migrating from KSP](/docs/migration/from-ksp-to-compiler-plugin).
- Modules loaded with `loadKoinModules` after start are not part of the compile-time graph check.

## Related

- [Modules](/docs/reference/koin-core/modules): organize definitions, `includes()`, `@Module` and `@Configuration`.
- [Definitions](/docs/reference/koin-core/definitions): declare what Koin creates, eager creation and overrides.
- [Lazy Modules](/docs/reference/koin-core/lazy-modules): load modules in the background to shorten startup.
- [Context Isolation](/docs/reference/koin-core/context-isolation): run Koin in an isolated container for SDKs.
- [Compiler Plugin Setup](/docs/setup/compiler-plugin): install the plugin needed for `startKoin<T>()`.
- [Starting Koin on Android](/docs/reference/koin-android/start): `androidContext()`, `androidLogger()` and AndroidX Startup.
