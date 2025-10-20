---
title: Releases & API Upgrade Guides
custom_edit_url: null
---

:::info
This page provides a comprehensive overview of every Koin main release, detailing the evolution of our framework to help you plan for upgrades and maintain compatibility.
:::

For each version, the document is structured into the following sections:

- `Kotlin`: Specifies the Kotlin version used for the release, ensuring clarity on language compatibility and enabling you to leverage the latest Kotlin features.
- `New`: Highlights the newly introduced features and improvements that enhance functionality and developer experience.
- `Experimental`: Lists APIs and features marked as experimental. These are under active development and subject to change based on community feedback.
- `Deprecated`: Identifies APIs and features that have been marked for deprecation, along with guidance on recommended alternatives, helping you prepare for future removals.
- `Breaking`: Details any changes that may break backward compatibility, ensuring that you are aware of necessary adjustments during migration.

This structured approach not only clarifies the incremental changes in each release but also reinforces our commitment to transparency, stability, and continuous improvement in the Koin project.

See [Api Stability Contract](api-stability.md) for more details.

## 4.1.1

:::note
Uses Kotlin `2.1.21`
:::

### New 🎉

`koin-compose-viewmodel-navigation`
- Enhanced `sharedKoinViewModel` with optional `navGraphRoute` parameter for better Compose Navigation support

`koin-core`
- Core resolver performance optimization - avoid unnecessary flattening with single scope resolution  
- Enhanced scope debugging with linked scope IDs display

### Library Updates 📚

- **Kotlin** 2.1.21 (from 2.1.20)
- **Ktor** 3.2.3 (from 3.1.3) 
- **Jetbrains Compose** 1.8.2 (from 1.8.0)
- **AndroidX**: Fragment 1.8.9, WorkManager 2.10.3, Lifecycle 2.9.3, Navigation 2.9.3
- **Testing**: Robolectric 4.15.1, Benchmark 0.4.14
- **Build**: Binary Validator 0.18.1, NMCP 1.1.0

### Bug Fixes 🐛

`koin-core`
- Reverted logger constraint causing compatibility errors
- Fixed Compose scope resolution with improved `LocalKoinApplication`/`LocalKoinScope` context handling

`koin-build`
- Fixed Maven Central publication issues

## 4.1.0

:::note
Uses Kotlin `2.1.20`
:::

### New 🎉

`koin-core`
- Configuration - `KoinConfiguration` API to help wrap configuration
- Scope - Introduce a new *Scope Archetype* of a dedicated Scope Type qualifier for the category of scope. Instance resolution can now be done against a scope category (aka Archetype)
- Feature Option - "Feature Option" to help feature flag new feature behavior inside Koin. You can activate an option with the `options` block in your Koin configuration:
```kotlin
startKoin {
    options(
        // activate a new feature
        viewModelScopeFactory()
    )
}
```
- Core - Introduce new `CoreResolver` that allows `ResolutionExtension` to help Koin resolve in external systems or resources (it's used to help wire Ktor DI)

`koin-android`
- Upgraded libraries (`androidx.appcompat:appcompat:1.7.0`, `androidx.activity:activity-ktx:1.10.1`) require raising Min SDK level from 14 to 21
- DSL - Added new Koin Module DSL extensions `activityScope`, `activityRetainedScope`, and `fragmentScope` to declare scope within Activity/Fragment
- Scope Functions - Also `activityScope()`, `activityRetainedScope()` and `fragmentScope()` API functions are now triggering Scope Archetypes

`koin-androidx-compose`
- Aligned with Koin Compose Multiplatform and all Compose 1.8 & Lifecycle 2.9

`koin-compose`
- Aligned with Compose 1.8 & Lifecycle 2.9
- New Function - `KoinApplicationPreview` to help render parallel preview in Android Studio & IntelliJ

`koin-compose-viewmodel`
- added `koinActivityViewModel` to allow set parent Activity as Host

`koin-ktor`
- Multiplatform - The module is now compiled in Kotlin KMP format. You can target `koin-ktor` from a multiplatform project.
- Merge - The Previous koin-ktor3 module has been merged into koin-ktor
- Extension - Introduces `Application.koinModule { }` and `Application.koinModules()` to let you declare Koin modules directly joined to a Ktor a module
```kotlin
fun Application.customerDataModule() {
    koinModule {
        singleOf(::CustomerRepositoryImpl) bind CustomerRepository::class
    }
}
```
- Scope - `Module.requestScope` - allows declaring definitions inside a Ktor request scope (avoid declaring `scope<RequestScope>` manually)
The injected scope is also allows to inject `ApplicationCall` in constructor.


`koin-core-coroutines`
- Module DSL - Introduce new `ModuleConfiguration` to help gather module configuration in one structure, to help better verify it later.
```kotlin
val m1 = module {
    single { Simple.ComponentA() }
}
val lm1 = lazyModule {
    single { Simple.ComponentB(get()) }
}
val conf = moduleConfiguration {
    modules(m1)
    lazyModules(lm1)
}
```
- Configuration DSL - Koin configuration can now use `ModuleConfiguration` to load modules:
```kotlin
startKoin {
    moduleConfiguration {
        modules(m1)
        lazyModules(lm1)
    }
}

// or even
val conf = moduleConfiguration {
    modules(m1)
    lazyModules(lm1)
}

startKoin {
    moduleConfiguration(conf)
}
```

`koin-test-coroutines`
- Added new `koin-test-coroutines` Koin module to introduce new coroutines-related Test API
- Extension - extend Verify API to let you check your Koin configuration with `moduleConfiguration`, and then verify Mix of Modules/Lazy Modules configuration:
```kotlin
val conf = moduleConfiguration {
    modules(m1)
    lazyModules(lm1)
}

conf.verify()

// if you want Android types (koin-android-test)
conf.verify(extraTypes = androidTypes)
```

`koin-core-annotations`
- Annotations - `@InjectedParam` or `@Provided` to tag a property to be considered as an injected parameter or dynamically provided. Used for now in `Verify` API, but may be used to help with lighter DSL declaration later.

### Experimental 🚧

`koin-core`
- Wasm - Use of Kotlin 2.1.20 UUID generation

`koin-core-viewmodel`
- DSL - Added Module DSL extension `viewModelScope`, to declare component scoped to ViewModel scope archetype
- Scope Function - Added function `viewModelScope()`, to create a scope for ViewModel (tied to ViewModel class). This API now uses `ViewModelScopeAutoCloseable` to use the `AutoCloseable` API to help declare a scope and close it. no need anymore to close ViewModel scope by hand
- Class - Updated `ScopeViewModel` class to give support for a ready-to-use ViewModel-scoped class (handle scope creation and closing)
- Feature Option - Constructor ViewModel injection with ViewModel's scope, requires activating Koin option `viewModelScopeFactory` :
```kotlin
startKoin {
    options(
        // activate a new ViewModel scope creation
        viewModelScopeFactory()
    )
}

// will inject Session from MyScopeViewModel's scope
class MyScopeViewModel(val session: Session) : ViewModel()

module {
    viewModelOf(::MyScopeViewModel)
    viewModelScope {
        scopedOf(::Session)
    }
}
```

`koin-compose`
- Compose Function - Added new `KoinMultiplatformApplication` function, to try proposing a Multiplatform Compose entry point

`koin-core-viewmodel-navigation`
- Navigation Extension - added `sharedViewModel` to reuse ViewModel instance from navigation's NavbackEntry

`koin-test`
- Annotations - The Koin configuration verification API `Verify` now helps you check for nullable, lazy, and list parameters. Simply use `@InjectedParam` or `@Provided` to tag a property to be considered as an injected parameter or dynamically provided. This avoids complex declaration in Verify API.
```kotlin
// now detected in Verify
class ComponentB(val a: ComponentA? = null)
class ComponentBParam(@InjectedParam val a: ComponentA)
class ComponentBProvided(@Provided val a: ComponentA)
```

### Deprecation ⚠️

`koin-android`
- `ScopeViewModel` is now deprecated to be used by `koin-core-viewmodel` `ScopeViewModel` class instead

`koin-compose`
- The Compose context API is no longer required, as the Koin context is properly prepared on the current default context. Following is deprecated and can be removed: `KoinContext`

`koin-androidx-compose`
- Jetpack compose context API is not required anymore, as Koin context is properly prepared on the current default context. Following is deprecated and can be removed: `KoinAndroidContext`

`koin-androidx-compose-navigation`
- Due to lifecycle lib update, the function `koinNavViewModel` is not needed, can be replaced with `koinViewModel`

`koin-core-viewmodel-navigation`
- Due to lifecycle lib update, the function `koinNavViewModel` is not needed, can be replaced with `koinViewModel`

`koin-ktor`
- Extension - `Application.koin` is now deprecated in favor of `Application.koinModules` and `Application.koinModule`

### Breaking 💥

`koin-android`
- All state old ViewModel API are now removed:
    - `stateViewModel()`,`getStateViewModel()`, use instead `viewModel()`
    - `getSharedStateViewModel()`, `sharedStateViewModel()`, use instead `viewModel()` or `activityViewModel()` for shared instance

`koin-compose`
- Old compose API functions are removed:
    - function `inject()` have been removed in favor of `koinInject()`
    - function `getViewModel()` has been removed in favor of `koinViewModel()`
    - function `rememberKoinInject()` has been moved into `koinInject()`,
- Function `rememberKoinApplication` is marked as `@KoinInternalAPI`

## 4.0.4

:::note
Uses Kotlin `2.0.21`
:::

All used lib versions are located in [libs.versions.toml](https://github.com/InsertKoinIO/koin/blob/main/projects/gradle/libs.versions.toml)

### New 🎉

`koin-core`
- `KoinPlatformTools.generateId()` - With this new version of Kotlin, we benefit from new `kotlin.uuid.uuid` API. The `KoinPlatformTools.generateId()` Koin function now uses this new API to generates real UUID over platforms.

`koin-viewmodel`
- Koin 4.0 introduces ViewModel DSL & API that mutualises the Google/Jetbrains KMP API. To avoid duplication over the codebase, the ViewModel API is now located in `koin-core-viewmodel` & `koin-core-viewmodel-navigation` projects.
- Import for ViewModel DSL is `org.koin.core.module.dsl.*`

Following APIs in the given projects are now stable.

`koin-core-coroutines` - all API is now stable
- all `lazyModules`
- `awaitAllStartJobs`, `onKoinStarted`, `isAllStartedJobsDone`
- `waitAllStartJobs`, `runOnKoinStarted`
- `KoinApplication.coroutinesEngine`
- `Module.includes(lazy)`
- `lazyModule()`
- `KoinPlatformCoroutinesTools`

### Experimental 🚧

`koin-test`
- `ParameterTypeInjection` - new API to help design dynamic parameter injection for `Verify` API

`koin-androidx-startup`
- `koin-androidx-startup` - New capacity to start Koin with `AndroidX Startup`, using `androidx.startup.Initializer` API. All API inside `koin-androidx-startup` are Experimental

`koin-compose`
- `rememberKoinModules` - load/unload Koin modules given a @Composable component
- `rememberKoinScope` - load/unload Koin Scope given a @Composable component
- `KoinScope` - Load Koin scope for all underlying Composable children

### Deprecation ⚠️

The following APIs have been deprecated and should not be used anymore:

- `koin-test`
    - All API for `checkModules`. Migrate to `Verify` API.

- `koin-android`
    - ViewModel DSL in favor of new centralized DSL in koin-core
    - All state ViewModel API are deprecated at the error level:
        - `stateViewModel()`,`getStateViewModel()`, use instead `viewModel()`
        - `getSharedStateViewModel()`, `sharedStateViewModel()`, use instead `viewModel()` or `activityViewModel()` for shared instance

`koin-compose`
- old compose API function are deprecated at error level:
    - function `inject()` is deprecated (error level) in favor of `koinInject()`
    - function `getViewModel()` is deprecated (error level) in favor of `koinViewModel()`
    - function `rememberKoinInject()` is deprecated (error level) in favor of `koinInject()`,

- `koin-compose-viewmodel`
    - ViewModel DSL in favor of new centralized DSL in koin-core
    - function `koinNavViewModel` is now deprecated, in favor of `koinViewModel`

### Breaking 💥

The following APIs have been removed, due to deprecations in last milestone:

:::note
all API annotated with `@KoinReflectAPI` has been removed
:::

`koin-core`
- `ApplicationAlreadyStartedException` has been renamed `KoinApplicationAlreadyStartedException`
- `KoinScopeComponent.closeScope()` removed, as not used anymore internally
- Moved internal `ResolutionContext` to replace `InstanceContext`
- `KoinPlatformTimeTools`, `Timer`, `measureDuration` removed, to use Kotlin Time API instead
- `KoinContextHandler` removed in favor of `GlobalContext`

`koin-android`
- function `fun Fragment.createScope()` is removed
- All API around ViewModel factory (internal mainly) are reworked for new internals

`koin-compose`
- removed `StableParametersDefinition` as not used  anymore in internals
- removed all Lazy ViewModel API - old `viewModel()`
- removed `rememberStableParametersDefinition()` as not used internally anymore

## 3.5.6

:::note
Uses Kotlin `1.9.22`
:::

All used lib versions are located in [libs.versions.toml](https://github.com/InsertKoinIO/koin/blob/3.5.6/projects/gradle/libs.versions.toml)

### New 🎉

`koin-core`
- `KoinContext` now has followings:
    - `fun loadKoinModules(module: Module, createEagerInstances: Boolean = false)`
    - `fun loadKoinModules(modules: List<Module>, createEagerInstances: Boolean = false)`
- `koinApplication()` function is now using several formats:
    - `koinApplication(createEagerInstances: Boolean = true, appDeclaration: KoinAppDeclaration? = null)`
    - `koinApplication(appDeclaration: KoinAppDeclaration?)`
    - `koinApplication(createEagerInstances: Boolean)`
- `KoinAppDeclaration` to help open declaration styles
- `KoinPlatformTimeTools` to use API Time for JS
- iOS - `synchronized` API to use Touchlab Lockable API

`koin-androidx-compose`
- new `KoinAndroidContext` to bind on current Koin context from Android environment

`koin-compose`
- new `KoinContext` context starter with current default context

`koin-ktor`
- now uses isolated context for Ktor instance (uses `Application.getKoin()` instead of default context)
- Koin plugin introduces new monitoring
- `RequestScope` to allow scope instance to a Ktor request

### Experimental 🚧

`koin-android`
- `ViewModelScope` introduce experimental API for ViewModel scope

`koin-core-coroutines` - introducing new API to load modules in background

### Deprecation ⚠️

`koin-android`
- `getLazyViewModelForClass()` API is super complex, and calling to default global context. Prefer stick to Android/Fragment API
- `resolveViewModelCompat()` is deprecated in favor of `resolveViewModel()`

`koin-compose`
- functions `get()` and `inject()` have been deprecated in favor of `koinInject()`
- functions `getViewModel()` has been deprecated in favor of `koinViewModel()`
- function `rememberKoinInject()` has been deprecated for `koinInject()`

### Breaking 💥

`koin-core`
- `Koin.loadModules(modules: List<Module>, allowOverride: Boolean = true, createEagerInstances : Boolean = false)` is replacing `Koin.loadModules(modules: List<Module>, allowOverride: Boolean = true)`
- Moved property `KoinExtension.koin` to function `KoinExtension.onRegister()`
- iOS - `internal fun globalContextByMemoryModel(): KoinContext` to use `MutableGlobalContext`

`koin-compose`
- function `KoinApplication(moduleList: () -> List<Module>, content: @Composable () -> Unit)` removed in favor of `KoinContext`, and `KoinAndroidContext`

## 3.4.3

:::note
Uses Kotlin `1.8.21`
:::

### New 🎉

`koin-core`
- New ExtensionManager API to help write extension engine for Koin - `ExtensionManager` + `KoinExtension`
- Parameters API update with `parameterArrayOf` & `parameterSetOf`

`koin-test`
- `Verification` API - to help run `verify` on a Module.

`koin-android`
- internals for ViewModel injection
- add `AndroidScopeComponent.onCloseScope()` function callback

`koin-android-test`
- `Verification` API - to help run `androidVerify()` on a Module.

`koin-androidx-compose`
- new `get()`
- new `getViewModel()`
- new Scopes `KoinActivityScope`, `KoinFragmentScope`

`koin-androidx-compose-navigation` - New module for navigation
- new `koinNavViewModel()`

`koin-compose` - New Multiplatform API for Compose
- `koinInject`, `rememberKoinInject`
- `KoinApplication`

### Experimental 🚧

`koin-compose` - New Experimental Multiplatform API for Compose
- `rememberKoinModules`
- `KoinScope`, `rememberKoinScope`

### Deprecation ⚠️

`koin-compose`
- functions `get()` to replace `inject()` usage avoiding Lazy function
- functions `getViewModel()` to replace `viewModel()` function, usage avoiding Lazy function

### Breaking 💥

`koin-android`
- `LifecycleScopeDelegate` is now removed

`koin-androidx-compose`
- Removed `getStateViewModel` in favor of `koinViewModel`
