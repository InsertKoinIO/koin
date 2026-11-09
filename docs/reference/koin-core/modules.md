---
title: Modules
description: Group definitions in modules, compose them with includes(), scan packages with @ComponentScan and auto-discover modules with @Configuration.
---

A module groups related definitions, by feature or by layer. This page covers how to declare modules, compose them, scan packages for annotated classes, discover modules across Gradle modules, and in which order modules load. For the definitions inside a module, see [Definitions](/docs/reference/koin-core/definitions).

## Declare a module

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val networkModule = module {
    single<ApiClient>()
    single<TokenManager>()
}

val dataModule = module {
    single<Database>()
    single<UserRepository>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module
@ComponentScan("com.myapp.network")
class NetworkModule

@Module
@ComponentScan("com.myapp.data")
class DataModule
```

A `@Module` class gathers the annotated classes found by `@ComponentScan`, and the provider functions declared inside it (see [Classes you don't own](/docs/reference/koin-core/definitions#classes-you-dont-own)).

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.dsl.module
import org.koin.core.module.dsl.*

val networkModule = module {
    singleOf(::ApiClient)
    singleOf(::TokenManager)
}

val dataModule = module {
    singleOf(::Database)
    singleOf(::UserRepository)
}
```

</TabItem>
</Tabs>

A definition can depend on a definition from another module. Koin resolves across every loaded module, so `UserRepository` in `dataModule` can use `ApiClient` from `networkModule` without any import between modules. Both modules must be loaded, see [Starting Koin](/docs/reference/koin-core/starting-koin).

## Module composition with `includes()`

A module can include other modules. Loading the parent loads the whole tree:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val networkModule = module {
    single<ApiClient>()
}

val storageModule = module {
    single<Database>()
}

val dataModule = module {
    includes(networkModule, storageModule)
    single<UserRepository>()
}

startKoin {
    modules(dataModule)
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module
class NetworkModule

@Module
class StorageModule

@Module(includes = [NetworkModule::class, StorageModule::class])
@ComponentScan("com.myapp.data")
class DataModule

@KoinApplication(modules = [DataModule::class])
class MyApp
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val networkModule = module {
    singleOf(::ApiClient)
}

val storageModule = module {
    singleOf(::Database)
}

val dataModule = module {
    includes(networkModule, storageModule)
    singleOf(::UserRepository)
}

startKoin {
    modules(dataModule)
}
```

</TabItem>
</Tabs>

Prefer a tree of `includes()` with one root module over a long flat list in `startKoin`. The structure of your modules then follows the structure of your code, and the start call stays short.

Koin flattens the tree and loads each module once, even if several parents include it:

```kotlin
val commonModule = module {
    single<Logger>()
}

val featureAModule = module {
    includes(commonModule)
    single<FeatureA>()
}

val featureBModule = module {
    includes(commonModule)
    single<FeatureB>()
}

val appModule = module {
    includes(featureAModule, featureBModule) // commonModule loads once
}
```

### Visibility in multi-module projects

In a Gradle multi-module project, expose one public module per feature and keep the others `private` or `internal`:

```kotlin
// Gradle module "feature-user"
private val userDataModule = module {
    single<UserDao>()
    single<UserCache>()
}

val userFeatureModule = module {
    includes(userDataModule)
    viewModel<UserViewModel>()
}
```

The app module loads `userFeatureModule` and never sees the internal modules. For a full layout of an Android app split in Gradle modules, see [Multi-Module Apps](/docs/reference/koin-android/multi-module).

## Scan packages with @ComponentScan

`@ComponentScan` is for annotations only. It collects every annotated class and top-level function in a package and its subpackages:

```kotlin
@Module
@ComponentScan
class AppModule // scans the package of AppModule

@Module
@ComponentScan("com.myapp.features")
class FeatureModule

@Module
@ComponentScan("com.myapp.data", "com.myapp.network")
class InfraModule
```

With no value, it scans the package of the annotated class. The scan crosses Gradle modules: classes from another Gradle module in the same package are found too.

With the DSL styles, you list each definition in the module yourself. To mix both, include a DSL module in your start block next to the annotated ones (see [Typed startup with @KoinApplication](/docs/reference/koin-core/starting-koin#typed-startup-with-koinapplication)).

## Auto-discovery with @Configuration

`@Configuration` marks a `@Module` so that `@KoinApplication` finds it without listing it. The module can live in any Gradle module on the classpath:

```kotlin
// Gradle module "feature-user"
@Module
@Configuration
@ComponentScan("com.myapp.feature.user")
class UserFeatureModule

// Gradle module "app"
@KoinApplication
class MyApp

fun main() {
    startKoin<MyApp>() // loads UserFeatureModule
}
```

Adding a feature then means adding the Gradle dependency: no change to the app's module list. The cost is that the list of loaded modules is no longer written in one place. If you want it explicit, list the modules in `@KoinApplication(modules = [...])` instead.

`@Configuration` takes labels, so you can load different modules per environment or flavor. The rules are on [Configuration labels](/docs/reference/koin-core/starting-koin#configuration-labels).

`@Configuration` is annotations only and needs `@KoinApplication` with typed startup (`startKoin<MyApp>()`).

## Load order and overrides

When two loaded modules declare the same type, the module loaded last wins. In a DSL start block, this is the order of `modules(...)`:

```kotlin
startKoin {
    modules(productionModule, debugModule) // debugModule's ApiService wins
}
```

With `@KoinApplication`, the Compiler Plugin builds the module list in this order:

1. The `@Configuration` modules it discovers, from your code and from your dependencies.
2. The modules listed in `@KoinApplication(modules = [...])`, in declaration order.

So the modules of your app override the defaults of a library:

```kotlin
// In a library
@Module
@Configuration
class CoreModule {
    @Singleton
    fun feature(): Feature = DefaultFeature()
}

// In the app
@Module
class AppModule {
    @Singleton
    fun feature(): Feature = AppFeature()
}

@KoinApplication(modules = [AppModule::class])
class MyApp // get<Feature>() returns AppFeature
```

Inside the explicit list, `modules = [A::class, B::class, C::class]` loads A, then B, then C, and each module's `includes` stay grouped with it. A module that is both listed and discovered by `@Configuration` loads once, at its position in the explicit list. To control the order between several `@Configuration` modules, list them explicitly.

To make accidental overrides fail at start, use `allowOverride(false)`. The rules for overriding a single definition are in [Overriding a definition](/docs/reference/koin-core/definitions#overriding-a-definition).

## Eager modules

Mark a whole module to create all its `single` definitions when Koin starts:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val coreModule = module(createdAtStart = true) {
    single<ConfigManager>()
    single<CrashReporter>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module(createdAtStart = true)
@ComponentScan("com.myapp.core")
class CoreModule
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val coreModule = module(createdAtStart = true) {
    singleOf(::ConfigManager)
    singleOf(::CrashReporter)
}
```

</TabItem>
</Tabs>

Each eager definition adds to startup time. See [Eager creation](/docs/reference/koin-core/definitions#eager-creation) for the per-definition flag and its cost.

## Parameterized modules

A DSL module is a value, so a function can build it from arguments:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
fun loggingModule(debug: Boolean) = module {
    if (debug) {
        single<DebugLogger>() bind Logger::class
    } else {
        single<ReleaseLogger>() bind Logger::class
    }
}

startKoin {
    modules(loggingModule(debug = BuildConfig.DEBUG))
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

A `@Module` class takes no arguments. Use a DSL module for this case, or one `@Module` per variant selected with [configuration labels](/docs/reference/koin-core/starting-koin#configuration-labels).

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
fun loggingModule(debug: Boolean) = module {
    single<Logger> {
        if (debug) DebugLogger() else ReleaseLogger()
    }
}

startKoin {
    modules(loggingModule(debug = BuildConfig.DEBUG))
}
```

</TabItem>
</Tabs>

The same idea swaps implementations: keep `repositoryModule` fixed, and load either `localDatasourceModule` or `remoteDatasourceModule` next to it, each declaring a `Datasource`. Conditional module lists are resolved at runtime, so the Compiler Plugin can't verify every combination at build time.

## Related

- [Definitions](/docs/reference/koin-core/definitions): what goes inside a module.
- [Starting Koin](/docs/reference/koin-core/starting-koin): load modules, typed startup and configuration labels.
- [Lazy Modules](/docs/reference/koin-core/lazy-modules): load modules in the background.
- [KMP Shared Modules](/docs/reference/koin-core/kmp-shared-modules): share modules between platforms.
- [Multi-Module Apps](/docs/reference/koin-android/multi-module): organize modules across Gradle modules on Android.
- [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): every parameter of `@Module`, `@ComponentScan` and `@Configuration`.
