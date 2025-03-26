---
title: Releases & API Compatibility Guide
custom_edit_url: null
---

:::info
This pages provides a comprehensive overview of every Koin main release, detailing the evolution of our framework to help you plan for upgrades and maintain compatibility.
:::

For each version, the document is structured into the following sections:

- `Kotlin`: Specifies the Kotlin version used for the release, ensuring clarity on language compatibility and enabling you to leverage the latest Kotlin features.
- `New`: Highlights the newly introduced features and improvements that enhance functionality and developer experience.
- `Experimental`: Lists APIs and features marked as experimental. These are under active development and subject to change based on community feedback.
- `Deprecated`: Identifies APIs and features that have been marked for deprecation, along with guidance on recommended alternatives, helping you prepare for future removals.
- `Breaking`: Details any changes that may break backward compatibility, ensuring that you are aware of necessary adjustments during migration.

This structured approach not only clarifies the incremental changes in each release but also reinforces our commitment to transparency, stability, and continuous improvement in the Koin project.

## 4.0.3

:::note
Uses Kotlin `2.0.21`
:::

All used lib versions are located in [libs.versions.toml](https://github.com/InsertKoinIO/koin/blob/main/projects/gradle/libs.versions.toml)

### New 🎉

`koin-core`
- `KoinPlatformTools.generateId()` - With this new version of Kotlin, we benefit from new `kotlin.uuid.uuid` API. The `KoinPlatformTools.generateId()` Koin function now uses this new API to generates real UUID over platforms.

`koin-viewmodel`
 - Koin 4.0 introduces new ViewModel DSL & API that mutualise the Google/Jetbrains KMP API. To avoid duplication over the codebase, the ViewModel API is now located in `koin-core-viewmodel` & `koin-core-viewmodel-navigation` projects.
 - Import for ViewModel DSL is `org.koin.core.module.dsl.*`

Following APIs in given projects, are now stable.

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

The following APIs have been deprecated, and should not be used anymore:

- `koin-test`
  - all API for `checkModules`. Migrate to `Verify` API.

- `koin-android` 
  - ViewModel DSL in favor of new centralized DSL in koin-core

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
  - all state ViewModel API are deprecated at error level: 
    - `stateViewModel()`,`getStateViewModel()`, use instead `viewModel()`
    - `getSharedStateViewModel()`, `sharedStateViewModel()`, use instead `viewModel()` or `activityViewModel()` for shared instance
  - function `fun Fragment.createScope()` is removed
  - All API around ViewModel factory (internal mainly) are reworked for new internals

`koin-compose`
  - old compose API function are deprecated at error level:
    - functions `inject()` have been removed in favor of `koinInject()`
    - functions `getViewModel()` has been removed in favor of `koinViewModel()`
    - function `rememberKoinInject()` has been moved into `koinInject()`, 
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








