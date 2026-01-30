---
title: Android Best Practices
---

This guide covers Android-specific best practices for memory management, security, and migration from Hilt.

:::info
For general module concepts, see **[Modules](/docs/reference/koin-core/modules)**. For scoping, see **[Scopes](/docs/reference/koin-core/scopes)** and **[Android Scopes](/docs/reference/koin-android/scope)**.
:::

## Memory Management

### Avoid Activity/Fragment Leaks

```kotlin
// ❌ Bad - Activity leak
module {
    single { SomeService(get<Activity>()) }  // Activity reference in singleton!
}

// ✅ Good - Use Application context
module {
    single { SomeService(androidContext()) }  // Application context, safe
}

// ✅ Good - Use activity scope
module {
    activityScope {
        scoped { SomeService(/* activity-scoped dependencies */) }
    }
}
```

### Close Scopes Properly

```kotlin
// ✅ Good - Automatic scope management
class MyActivity : ScopeActivity() {
    override val scope: Scope by activityScope()
    // Scope automatically closed in onDestroy
}

// ❌ Bad - Manual scope without cleanup
class MyActivity : AppCompatActivity() {
    private val myScope = createScope<MyActivity>()
    // Scope never closed - memory leak!
}

// ✅ Good - Manual scope with cleanup
class MyActivity : AppCompatActivity() {
    private lateinit var myScope: Scope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myScope = createScope<MyActivity>()
    }

    override fun onDestroy() {
        myScope.close()
        super.onDestroy()
    }
}
```

### Clear References in Long-Lived Objects

```kotlin
// ❌ Bad - Holding references to UI
class UserRepository {
    private val listeners = mutableListOf<UserUpdateListener>()  // Might hold Activity refs

    fun addListener(listener: UserUpdateListener) {
        listeners.add(listener)
    }
}

// ✅ Good - Weak references or manual cleanup
class UserRepository {
    private val listeners = mutableListOf<WeakReference<UserUpdateListener>>()

    fun addListener(listener: UserUpdateListener) {
        listeners.add(WeakReference(listener))
    }

    fun removeListener(listener: UserUpdateListener) {
        listeners.removeAll { it.get() == listener || it.get() == null }
    }
}
```

## Android Debugging

### Enable Android Logger

```kotlin
startKoin {
    androidLogger(Level.DEBUG)  // See all Koin operations
    androidContext(this@MyApplication)
    modules(appModules)
}
```

### Verify Modules in Debug Builds

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(allModules)
        }

        // Use verify() in unit tests instead
        // appModule.verify()
    }
}
```

### Scope Callbacks for Debugging

```kotlin
class DebugActivity : ScopeActivity() {
    override val scope: Scope by activityScope()

    init {
        scope.registerCallback(object : ScopeCallback {
            override fun onScopeClose(scope: Scope) {
                Log.d("Koin", "Scope ${scope.id} closing")
            }
        })
    }
}
```

## Security Best Practices

### Don't Store Secrets in Modules

```kotlin
// ❌ Bad - Hardcoded secrets
module {
    single {
        Retrofit.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("API-Key", "super-secret-key")  // NO!
                        .build()
                )
            }
            .build()
    }
}

// ✅ Good - Secrets from secure storage
module {
    single {
        val securePrefs = get<SecurePreferences>()
        Retrofit.Builder()
            .addInterceptor(AuthInterceptor(securePrefs))
            .build()
    }
}
```

## Migration from Dagger/Hilt

:::info
Koin supports JSR-330 annotations (`@Singleton`, `@Inject`, `@Named`) from `jakarta.inject`. You can keep using familiar annotations. See [JSR-330 Compatibility](/docs/reference/koin-android/jsr330).
:::

### Annotation Mapping

| Hilt | Koin Annotations |
|------|------------------|
| `@Singleton` | `@Singleton` (JSR-330 compatible) |
| `@Provides` | `@Factory` |
| `@Binds` | `@Singleton ... bind Interface::class` |
| `@Inject` | `@Inject` (JSR-330 compatible) |
| `@HiltViewModel` | `@KoinViewModel` |
| `@InstallIn(SingletonComponent)` | `@Module` + `@ComponentScan` |
| `@InstallIn(ActivityComponent)` | `@Scope(ActivityScope::class)` |

### Example Migration

```kotlin
// Before (Hilt)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: ApiService
) : UserRepository

// After (Koin) - minimal changes!
@KoinViewModel
class HomeViewModel(
    private val repository: UserRepository
) : ViewModel()

@Singleton  // Keep using JSR-330
class UserRepositoryImpl(
    private val api: ApiService
) : UserRepository
```

### Module Migration

```kotlin
// Before (Hilt)
@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder().build()
}

// After (Koin Annotations)
@Module
class NetworkModule {
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder().build()
}
```

### Gradual Migration

```kotlin
// Step 1: Add Koin alongside Hilt for new features
@KoinViewModel
class NewFeatureViewModel(
    private val repository: NewFeatureRepository
) : ViewModel()

@Singleton
class NewFeatureRepository(private val api: ApiService)

// Step 2: Migrate existing features one by one
@Singleton
class MigratedRepository(private val api: ApiService) : UserRepository

// Step 3: Remove Hilt when migration complete
```

## See Also

- **[Scopes](/docs/reference/koin-core/scopes)** - Core scoping concepts
- **[Android Scopes](/docs/reference/koin-android/scope)** - Android lifecycle scopes
- **[Testing](/docs/reference/koin-android/instrumented-testing)** - Android testing guide
- **[Multi-Module Apps](/docs/reference/koin-android/multi-module)** - Organizing Android modules
