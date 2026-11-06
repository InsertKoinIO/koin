---
title: Advanced Android Patterns
description: Pick implementations per build variant with BuildConfig or source sets, and declare UI helpers that need the Activity.
---

This page collects Android-only patterns: choosing an implementation per build type or flavor, and declaring UI helpers that need the current Activity. Patterns that work on every platform (collections, generics, decorators, providers) are in [Advanced Patterns](/docs/reference/koin-core/advanced-patterns). Scope linking is in [Scopes](/docs/reference/koin-core/scopes).

## Implementations per build variant

### With BuildConfig

Choose the implementation in a provider function:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.plugin.module.dsl.create

fun analytics(context: Context): Analytics =
    if (BuildConfig.ENABLE_ANALYTICS) FirebaseAnalyticsTracker(context) else NoOpAnalytics()

val analyticsModule = module {
    single { create(::analytics) }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module
class AnalyticsModule {

    @Singleton
    fun analytics(context: Context): Analytics =
        if (BuildConfig.ENABLE_ANALYTICS) FirebaseAnalyticsTracker(context) else NoOpAnalytics()
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val analyticsModule = module {
    single<Analytics> {
        if (BuildConfig.ENABLE_ANALYTICS) FirebaseAnalyticsTracker(androidContext()) else NoOpAnalytics()
    }
}
```

</TabItem>
</Tabs>

`ENABLE_ANALYTICS` is a `buildConfigField` set per build type or flavor. `BuildConfig.DEBUG` works the same way. Both implementations are compiled into every variant; only one is created.

### With source sets

To keep debug-only code out of the release APK, declare the same Koin module in each variant source set:

```kotlin
// src/debug/kotlin/com/app/di/VariantModule.kt
val variantModule = module {
    single<Logger> { VerboseLogger() }
    single<NetworkInspector> { ChuckerInspector(androidContext()) }
}
```

```kotlin
// src/release/kotlin/com/app/di/VariantModule.kt
val variantModule = module {
    single<Logger> { CrashlyticsLogger() }
    single<NetworkInspector> { NoOpInspector() }
}
```

```kotlin
// src/main
startKoin {
    androidContext(this@MainApplication)
    modules(appModule, variantModule)
}
```

`src/main` sees one `variantModule` per build, so it compiles in both. Each variant only ships its own implementations and their libraries (use `debugImplementation` for debug-only dependencies).

## UI helpers that need the Activity

Some helpers need the current Activity, not the Application: dialogs, permission requests, anything that uses the Activity theme. An `AlertDialog` built with the Application context fails when it is shown. Declare these helpers in the Activity scope, where `Context` resolves to the Activity:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class DialogProvider(private val context: Context) {

    fun confirm(title: String, onConfirm: () -> Unit): AlertDialog =
        AlertDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok) { _, _ -> onConfirm() }
            .create()
}

val uiModule = module {
    activityScope {
        scoped<DialogProvider>()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@ActivityScope
class DialogProvider(private val context: Context) {

    fun confirm(title: String, onConfirm: () -> Unit): AlertDialog =
        AlertDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton(android.R.string.ok) { _, _ -> onConfirm() }
            .create()
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val uiModule = module {
    activityScope {
        scopedOf(::DialogProvider)
    }
}
```

</TabItem>
</Tabs>

```kotlin
class SettingsActivity : ScopeActivity() {

    private val dialogs: DialogProvider by inject()

    fun onResetClicked() {
        dialogs.confirm("Reset settings?") { resetSettings() }.show()
    }
}
```

The Activity must have a scope (`ScopeActivity` or `AndroidScopeComponent`). From an Activity without a scope, `DialogProvider` is not found. The scope closes with the Activity, so the helper never outlives it. See [Android Scopes](/docs/reference/koin-android/scope).

Don't declare such a helper as a `single`: a `single` resolves its dependencies from the root scope, where `Context` is the Application, and it would outlive the Activity anyway.

## Related

- [Advanced Patterns](/docs/reference/koin-core/advanced-patterns): collections, generics, decorators and providers on every platform.
- [Android Scopes](/docs/reference/koin-android/scope): Activity and Fragment scopes, and what `Context` resolves to.
- [Android Best Practices](/docs/reference/koin-android/best-practices): Context leaks and memory.
- [Troubleshooting](/docs/reference/koin-core/troubleshooting): circular dependencies and resolution errors.
