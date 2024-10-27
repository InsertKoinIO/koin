---
title: Migration Guides
---

All you need to setting up Koin in your project

## From Koin 3.x to Koin 4.0

### Kotlin Version

:::info
Koin 4.0 requires Kotlin `2.0.20`. Be sure to have the right Kotlin version if you see some compilation problems.
:::

With this new version of Kotlin, we benefit from new `kotlin.uuid.uuid` API. The `KoinPlatformTools.generateId()` Koin function now uses this new API to generates real UUID over platforms.


### New ViewModel DSL & API

Koin 4.0 introduces new ViewModel DSL & API that mutualize the Google/Jetbrains KMP API. To avoid duplication over the codebase, 
the ViewModel API is now located in `koin-core-viewmodel` & `koin-core-viewmodel-navigation` projects.

You don't have to change your package, current ones are already using those new `koin-core-viewmodel` packages. You just need to change your import like follow:

```kotlin
org.koin.core.module.dsl.*
```

### Deprecations

The following APIs have been deprecated, and should not be used anymore:

- `koin-test` - checkModules
- `koin-android` - ViewModel DSL in favor of new centralized DSL in koin-core 
- `koin-compose-viewmodel` - ViewModel DSL in favor of new centralized DSL in koin-core 


### Removals

The following APIs have been removed, due to deprecations in last milestone:

- `koin-core` - all API tagged with @KoinReflectAPI
- `koin-android` - all state ViewModel API (stateViewModel, sharedStateViewModel ...)
- `koin-compose` - functions `get` and `inject` have been removed in favor of `koinInject`

class `ApplicationAlreadyStartedException` has been renamed `KoinApplicationAlreadyStartedException`
