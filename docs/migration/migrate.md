---
title: Migration Guide
---

The new branch of Koin 3.x is bringing latest features & fixes. It brings Multiplatform, Jetpack Compose and much more. You can use it even if you don't use those features, to benefit from the latest fixes.

## Migrating to latest version 🚀

### Koin v2

#### Android & AndroidX Module

The following modules are available for Android. Be sure to use the `koin-androidx` modules, as their are based on the latest AndroidX library support:

```groovy
// Koin AndroidX Scope features
implementation "io.insert-koin:koin-androidx-scope:$koin_version"
// Koin AndroidX ViewModel features
implementation "io.insert-koin:koin-androidx-viewmodel:$koin_version"
// Koin AndroidX Fragment features
implementation "io.insert-koin:koin-androidx-fragment:$koin_version"
// Koin AndroidX WorkManager
implementation "io.insert-koin:koin-androidx-workmanager:$koin_version"
// Koin AndroidX Jetpack Compose
implementation "io.insert-koin:koin-androidx-compose:$koin_version"
// Koin AndroidX Experimental features
implementation "io.insert-koin:koin-androidx-ext:$koin_version"
```

### Koin v3

The new Koin core is now Multiplatform. You can use it even if your project is not Kotlin Multiplatform. 

#### Reimporting for new API

If you seen unresolved Koin API import, you can try to remove/reimport the used API as some packages has been moved in the Koin core module. See new section 

#### Kotlin Core & JVM features

KoinComponent and linked extension have been moved to package `org.koin.core.component`. Please reimport related APIs (`KoinComponent`,`inject`, `get`...).

Java/JVM Specific API are isolated in `koin-core-jvm` part of Koin core. Here are the following components:

- PropertyRegistry:
    - `saveProperties`
    - `loadPropertiesFromFile`
    - `loadEnvironmentProperties`

- KoinJavaComponent

- KoinApplication:
    - `fileProperties`
    - `environmentProperties`

- Scope extensions for Java: 
    - `org.koin.core.scope.ScopeJVMKt`

#### Android Modules

In v3, the `koin-android` modules is using AndroidX API, and merge also all Scope/Fragment/ViewModel API. You don't need anymore to specify `koin-androidx-fragment`, `koin-androidx-viewmodel` or `koin-androidx-scope` as their are all in the `koin-android` module:

```groovy
// Koin main features for Android (Scope,ViewModel ...)
implementation "io.insert-koin:koin-android:$koin_version"
// Koin Java Compatibility
implementation "io.insert-koin:koin-android-compat:$koin_version"
// Koin for Jetpack WorkManager
implementation "io.insert-koin:koin-androidx-workmanager:$koin_version"
// Koin for Jetpack Compose
implementation "io.insert-koin:koin-androidx-compose:$koin_version"
```

Java Compat API has been extracted in `koin-android-compat` module.
