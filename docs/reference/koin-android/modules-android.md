---
title: Android Module Loading
---

This guide covers Android-specific module loading with `androidContext()` and `androidLogger()`.

:::info
For core module concepts (declaration, includes, overrides), see [Modules](/docs/reference/koin-core/modules). For lazy module loading, see [Lazy Modules](/docs/reference/koin-core/lazy-modules).
:::

## Starting Koin on Android

### With Annotations

```kotlin
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

### With DSL

```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Android logger
            androidLogger()
            // or with level
            androidLogger(Level.DEBUG)

            // Android context
            androidContext(this@MainApplication)

            // Modules
            modules(appModule, networkModule, dataModule)
        }
    }
}
```

## Android-Specific Functions

| Function | Description |
|----------|-------------|
| `androidContext()` | Provides Application context in definitions |
| `androidApplication()` | Provides Application instance in definitions |
| `androidLogger()` | Android Logcat logger for Koin |

### Using Android Context

```kotlin
val androidModule = module {
    single { DatabaseHelper(androidContext()) }
    single { SharedPrefsManager(androidContext()) }
    single { NotificationHelper(androidApplication()) }
}
```

## Dynamic Module Loading

Load or unload modules at runtime based on Activity lifecycle:

```kotlin
class FeatureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Load feature-specific dependencies
        loadKoinModules(featureModule)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up when leaving feature
        unloadKoinModules(featureModule)
    }
}
```

### Use Cases

- **Premium features** - Load only when user has subscription
- **Debug tools** - Load only in debug builds
- **Optional features** - Load on-demand

```kotlin
// Premium feature module
val premiumModule = module {
    viewModel<PremiumViewModel>()
    single<PremiumRepository>()
}

class PremiumActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (userHasPremium()) {
            loadKoinModules(premiumModule)
        }
        super.onCreate(savedInstanceState)
    }
}
```

## Lazy Loading on Android

For background module loading, use lazy modules:

```kotlin
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)

            // Critical modules load immediately
            modules(coreModule)

            // Non-critical modules load in background
            lazyModules(analyticsModule, syncModule)
        }
    }
}
```

:::info
For complete lazy modules documentation including parallel loading, see [Lazy Modules](/docs/reference/koin-core/lazy-modules).
:::

## Next Steps

- **[Modules](/docs/reference/koin-core/modules)** - Core module concepts
- **[Lazy Modules](/docs/reference/koin-core/lazy-modules)** - Background loading
- **[Multi-Module Apps](/docs/reference/koin-android/multi-module)** - Gradle multi-module architecture
