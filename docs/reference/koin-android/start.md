---
title: Starting Koin on Android
description: Start Koin from your Application class, provide the Android Context, configure logging and properties, or start with AndroidX App Startup.
---

This page shows how to start Koin in an Android app: the `Application` class, the Android `Context`, the Logcat logger, properties from assets, module loading after start, and AndroidX App Startup. It assumes the `io.insert-koin:koin-android` dependency is in your build (see [Gradle Setup](/docs/setup/gradle)). For what `startKoin` does on every platform, see [Starting Koin](/docs/reference/koin-core/starting-koin).

## Start Koin in your Application class

Start Koin once, in `Application.onCreate()`:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

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

`appModule` is a `module { }` declared with the Compiler Plugin DSL (`single<UserRepository>()`, `viewModel<UserViewModel>()`).

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin<MainApplication> {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}
```

`startKoin<MainApplication>()` loads every `@Module` class tagged with `@Configuration`. You don't list modules yourself. To pick modules explicitly, use `@KoinApplication(modules = [AppModule::class])`. Module discovery is explained in [Modules](/docs/reference/koin-core/modules).

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

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

</TabItem>
</Tabs>

Register the class in `AndroidManifest.xml` with `android:name=".MainApplication"` on the `<application>` tag, or Android never runs it.

`androidContext()` adds the `Application` instance to Koin, bound to both `Application` and `Context`. Call it before any option that needs a `Context`, such as `androidFileProperties()` or `workManagerFactory()`.

## Android Context in definitions

A class that needs a `Context` declares it in its constructor. No qualifier is needed: Koin resolves `Context` to the instance passed to `androidContext()`.

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class PreferencesStore(private val context: Context)
class NotificationHelper(private val application: Application)

val androidModule = module {
    single<PreferencesStore>()
    single<NotificationHelper>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class PreferencesStore(private val context: Context)

@Singleton
class NotificationHelper(private val application: Application)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val androidModule = module {
    singleOf(::PreferencesStore)
    singleOf(::NotificationHelper)
}
```

In a lambda definition, read them with `androidContext()` and `androidApplication()`: `single { PreferencesStore(androidContext()) }`.

</TabItem>
</Tabs>

| Function | Returns | Fails when |
|----------|---------|------------|
| `androidContext()` | The closest `Context`: the `Application`, or the Activity in an Activity scope | No `androidContext()` was given at start (`MissingAndroidContextException`) |
| `androidApplication()` | Always the `Application` instance | No `Application` was given to `androidContext()` |

Both functions are shortcuts for `get<Context>()` and `get<Application>()`, from `org.koin.android.ext.koin`.

### Which Context you get

In a `single`, or any definition resolved from the root scope, `Context` is the `Application`. This is safe for objects that live as long as the app.

In a definition resolved from an Activity scope, `Context` resolves to the Activity first, because the Activity is the source of that scope. This is how a scoped presenter can get the Activity. See [Android Scopes](/docs/reference/koin-android/scope).

### Activity context

Don't declare an `Activity` as a dependency of a `single`: the singleton keeps the Activity alive after it is destroyed, and the Activity leaks. When a class needs the Activity (screen metrics, a dialog), create it in the Activity, pass the Activity as an [injected parameter](/docs/reference/koin-core/injection-parameters), or declare it in an Activity scope:

```kotlin
class ScreenMetrics(private val activity: Activity) {
    fun screenWidth(): Int = activity.resources.displayMetrics.widthPixels
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val metrics = ScreenMetrics(this)
    }
}
```

### Several instances of the same Android type

When two definitions share a type, such as two `SharedPreferences` files, tell them apart with a [qualifier](/docs/reference/koin-core/qualifiers):

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val preferencesModule = module {
    single(named("settings")) {
        androidContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    single(named("session")) {
        androidContext().getSharedPreferences("session", Context.MODE_PRIVATE)
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module
class PreferencesModule {

    @Singleton
    @Named("settings")
    fun settings(context: Context): SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    @Singleton
    @Named("session")
    fun session(context: Context): SharedPreferences =
        context.getSharedPreferences("session", Context.MODE_PRIVATE)
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val preferencesModule = module {
    single(named("settings")) {
        androidContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    single(named("session")) {
        androidContext().getSharedPreferences("session", Context.MODE_PRIVATE)
    }
}
```

</TabItem>
</Tabs>

## Logging

`androidLogger()` sends Koin logs to Logcat. The default level is `Level.INFO`:

```kotlin
startKoin {
    androidLogger(Level.DEBUG)
    androidContext(this@MainApplication)
    modules(appModule)
}
```

`Level.DEBUG` logs every resolution and its duration, which helps when you debug a missing definition. It also slows resolution down, so keep it for debug builds: `androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)`. Other loggers are listed in [Starting Koin](/docs/reference/koin-core/starting-koin).

## Properties from assets

`androidFileProperties()` loads Koin properties from a file in `src/main/assets`. The default file name is `koin.properties`:

```kotlin
startKoin {
    androidContext(this@MainApplication)
    androidFileProperties()                  // assets/koin.properties
    // androidFileProperties("app.properties")
    modules(appModule)
}
```

It reads the file through the `Context`, so call it after `androidContext()`. If the file is missing, Koin logs it and continues. To read a property in a definition, see [Starting Koin](/docs/reference/koin-core/starting-koin).

## Loading modules after start

`loadKoinModules()` and `unloadKoinModules()` add or remove modules while the app runs. On Android, this fits a feature that is only used from a few screens:

```kotlin
private val premiumModules = lazy { loadKoinModules(premiumModule) }

class PremiumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        premiumModules.value
        super.onCreate(savedInstanceState)
    }
}
```

- Load before `super.onCreate()`: Fragments restored by the system then find their dependencies.
- Load once per process: loading the same module again re-registers its definitions, logs an override warning, and fails if Koin runs with `allowOverride(false)`.
- Unload with care: `unloadKoinModules(premiumModule)` drops the module definitions and their instances. Unload only when the feature is no longer needed, never on a configuration change, and track the loaded state yourself if you load it again later.

## Lazy modules

On large apps, loading every module in `Application.onCreate()` adds to startup time on the main thread. `lazyModules()` loads the modules you mark with `lazyModule { }` on a background thread, while the modules needed for the first screen load with `modules()`. Setup, waiting for completion and limits are in [Lazy Modules](/docs/reference/koin-core/lazy-modules).

## Start with AndroidX App Startup

[AndroidX App Startup](https://developer.android.com/topic/libraries/app-startup) runs initializers from one ContentProvider, before `Application.onCreate()`. The `io.insert-koin:koin-androidx-startup` artifact starts Koin as one of those initializers. Implement `KoinStartup` in your `Application` and return the configuration from `onKoinStartup()`:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.androix.startup.KoinStartup
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class MainApplication : Application(), KoinStartup {

    override fun onKoinStartup() = koinConfiguration {
        androidContext(this@MainApplication)
        modules(appModule)
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.androix.startup.KoinStartup
import org.koin.plugin.module.dsl.koinConfiguration

@KoinApplication
@OptIn(KoinExperimentalAPI::class)
class MainApplication : Application(), KoinStartup {

    override fun onKoinStartup() = koinConfiguration<MainApplication> {
        androidContext(this@MainApplication)
    }
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.androix.startup.KoinStartup
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class MainApplication : Application(), KoinStartup {

    override fun onKoinStartup() = koinConfiguration {
        androidContext(this@MainApplication)
        modules(appModule)
    }
}
```

</TabItem>
</Tabs>

`KoinStartup` replaces the `startKoin` call in `onCreate()`: don't call both. The package name is `org.koin.androix.startup` (with this spelling), and the API is marked `@KoinExperimentalAPI`.

If another initializer needs Koin, make it depend on `KoinInitializer`. App Startup creates initializers itself, so an initializer is an entry point: it implements `KoinComponent` to retrieve its dependencies.

```kotlin
class CrashTrackerInitializer : Initializer<Unit>, KoinComponent {

    private val crashTracker: CrashTracker by inject()

    override fun create(context: Context) {
        crashTracker.configure(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf(KoinInitializer::class.java)
}
```

:::warning
`KoinStartup` still runs on the main thread, during app startup. It does not make Koin start faster. Use it only to order Koin with other App Startup initializers. To move module loading off the main thread, use [Lazy Modules](/docs/reference/koin-core/lazy-modules).
:::

## Starting Koin outside the Application class

You can call `startKoin` from another class, as long as it runs once per process and before the first injection. Pass the application context, not an Activity:

```kotlin
startKoin {
    androidContext(context.applicationContext)
    modules(appModule)
}
```

`startKoin` registers a global instance. A library or SDK that ships its own Koin setup should not use it, because it would conflict with the host app: use an isolated context instead (see [Context Isolation](/docs/reference/koin-core/context-isolation)).

## Related

- [Starting Koin](/docs/reference/koin-core/starting-koin): `startKoin` options on every platform, loggers and properties.
- [Android Entry Points](/docs/reference/koin-android/entry-points): inject in Activity, Fragment, Service and other components.
- [Android Scopes](/docs/reference/koin-android/scope): bind instances to an Activity or Fragment lifecycle.
- [Modules](/docs/reference/koin-core/modules): organize definitions and use `@Configuration` discovery.
- [Lazy Modules](/docs/reference/koin-core/lazy-modules): load modules in the background.
- [Multi-Module Apps](/docs/reference/koin-android/multi-module): organize Koin modules across Gradle modules.
