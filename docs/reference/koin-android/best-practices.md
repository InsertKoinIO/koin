---
title: Android Best Practices
description: Avoid Activity and Context leaks, close scopes, debug Koin on Android and keep secrets out of your modules.
---

This page lists the Android-specific mistakes that cause leaks, crashes or exposed secrets with Koin, and how to avoid them. It assumes you know [Android Scopes](/docs/reference/koin-android/scope) and [Starting Koin on Android](/docs/reference/koin-android/start). General module organization is in [Modules](/docs/reference/koin-core/modules), and error messages are explained in [Troubleshooting](/docs/reference/koin-core/troubleshooting).

## Memory and Context leaks

An instance leaks when something that lives longer keeps a reference to it. On Android, the usual victim is an Activity: a `single` lives as long as the process, an Activity only until the next rotation.

### Activity passed into a single

```kotlin
// leaks: the single keeps the first Activity forever
class UserActivity : AppCompatActivity() {
    private val tracker: ScreenTracker by inject { parametersOf(this) }
}

val appModule = module {
    single { params -> ScreenTracker(params.get<Activity>()) }
}
```

A `single` is created once, with the parameters of the first call. Later calls get the same instance, still holding the first Activity. Use one of these instead:

- Application context: when the class only needs resources or system services, declare it as a `single` with `Context`. In a `single`, `Context` is the `Application`.
- Activity scope: when the class needs the Activity, declare it in `activityScope { }` or with `@ActivityScope`. It is created per Activity and released with it.
- Factory with parameters: when the caller passes the Activity, use a `factory` and don't keep the result beyond the Activity lifetime.

The Compiler Plugin treats `Context`, `Activity`, `Application`, `Fragment` and `SavedStateHandle` as types provided by the platform, so it doesn't report them as missing. It also can't tell whether an `Activity` parameter is safe in a given definition: check this in review.

### Listeners held by singletons

```kotlin
class UserRepository {
    private val listeners = mutableListOf<UserListener>()

    fun addListener(listener: UserListener) { listeners += listener }
    fun removeListener(listener: UserListener) { listeners -= listener }
}
```

A repository is a `single`. If an Activity or Fragment registers itself as a listener and never removes it, the repository keeps the screen alive. Remove listeners in the matching lifecycle callback (`onStop()` for `onStart()`, `onDestroyView()` for `onViewCreated()`), or expose a `Flow` and collect it with `repeatOnLifecycle`, which cancels collection for you.

### Scoped instances that outlive their owner

- Retained scope: `activityRetainedScope` survives rotation. Its instances must not reference the Activity, a View or a Fragment.
- Fragment scope: it closes in `onDestroy()`, after `onDestroyView()`. Don't keep Views or bindings in Fragment-scoped instances.
- ViewModel: never inject Activity-scoped or Fragment-scoped instances into a ViewModel. The ViewModel survives rotation, the Activity scope does not.

These rules are explained in [Android Scopes](/docs/reference/koin-android/scope).

### Scopes you create yourself

`activityScope()`, `fragmentScope()`, `ScopeActivity`, `ScopeFragment` and `ScopeService` close their scope with the lifecycle. A scope created with `getKoin().createScope()` has no lifecycle: close it yourself, or every instance in it stays in memory.

```kotlin
// on login
val sessionScope = getKoin().createScope<UserSession>(userId)

// on logout
sessionScope.close()
```

## Debugging on Android

Turn on the Logcat logger with the debug level while you investigate:

```kotlin
startKoin {
    androidLogger(Level.DEBUG)
    androidContext(this@MainApplication)
    modules(appModule)
}
```

Filter Logcat on the `[Koin]` tag. At debug level, Koin logs each module load, each resolution and its duration, and each scope that opens or closes. Use `Level.ERROR` or `Level.NONE` in release builds.

Errors you only see on Android:

| Error | Usual cause |
|-------|-------------|
| `MissingAndroidContextException` | `androidContext()` is missing from `startKoin` |
| `KoinApplication has not been started` | The `Application` class is not declared in the manifest, or a ContentProvider injects in its `onCreate()` (see [Android Entry Points](/docs/reference/koin-android/entry-points#contentprovider)) |
| `NoDefinitionFoundException` for a scoped type | The Activity or Fragment has no scope (`ScopeActivity`, `AndroidScopeComponent`) |
| `ClosedScopeException` | Code resolves a scoped instance in or after `onDestroy()`, when the scope is already closed. Do that cleanup in `onCloseScope()` |
| Workers fail with "Could not instantiate" | The default WorkManager initializer is still in the manifest (see [WorkManager](/docs/reference/koin-android/workmanager)) |

With the Compiler Plugin, missing definitions are reported at build time instead (see [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety)).

:::tip
To see resolution times, scope lifetimes and Koin errors from real user sessions, see [Production Monitoring](/docs/tooling/production-monitoring).
:::

## Secrets

An APK can be unpacked. Anything compiled into it or stored in `assets/` can be read:

- Hardcoded keys: don't write API keys as string literals in a module.
- Koin properties: `androidFileProperties()` reads `assets/koin.properties`, which ships inside the APK. Use it for configuration, never for secrets.
- `BuildConfig` fields: they are compiled into the APK too. They hide a value from source control, not from the APK.

Fetch secrets at runtime from your backend, or keep tokens in encrypted storage, and inject the storage instead of the value:

```kotlin
class AuthInterceptor(private val tokens: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request().newBuilder()
                .header("Authorization", "Bearer ${tokens.currentToken()}")
                .build()
        )
}
```

`TokenStore` is a `single` backed by your encrypted storage, and `AuthInterceptor` is added to the `OkHttpClient` definition (see [Android Library Integration](/docs/reference/koin-android/binding-patterns)).

## Migrating from Hilt or Dagger

Annotation mapping, module migration and gradual migration are covered in [Migrating from Hilt](/docs/reference/koin-android/hilt-migration). To keep `@Inject` and `jakarta.inject` annotations, see [JSR-330 Annotations](/docs/reference/koin-android/jsr330).

## Related

- [Android Scopes](/docs/reference/koin-android/scope): lifetimes of Activity, Fragment, retained and ViewModel scopes.
- [Starting Koin on Android](/docs/reference/koin-android/start): `androidContext()`, logging and properties.
- [Troubleshooting](/docs/reference/koin-core/troubleshooting): resolution errors and circular dependencies.
- [Lazy Modules](/docs/reference/koin-core/lazy-modules): reduce Koin work at app startup.
- [Migrating from Hilt](/docs/reference/koin-android/hilt-migration): move an existing Hilt app to Koin.
