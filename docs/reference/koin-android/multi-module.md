---
title: Multi-Module Android Apps
---

This guide covers Android-specific aspects of multi-module architecture with Koin.

:::info
For core module concepts (`includes()`, organization, overrides), see [Modules](/docs/reference/koin-core/modules).
:::

## Android Application Setup

### With Annotations

```kotlin
@KoinApplication(AppModule::class)
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin<MyApplication> {
            androidLogger()
            androidContext(this@MyApplication)
        }
    }
}

// Root module includes all features
@Module(includes = [LoginModule::class, HomeModule::class, ProfileModule::class])
class AppModule
```

### With DSL

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)  // Single root module
        }
    }
}

// Root module includes all features
val appModule = module {
    includes(
        loginModule,
        homeModule,
        profileModule
    )
}
```

## Feature Module Example

```kotlin
// :feature:login module
@KoinViewModel
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val userRepository: UserRepository
) : ViewModel()

@Factory
class LoginUseCase(private val authService: AuthService)

@Module(includes = [DataModule::class])
@ComponentScan("com.app.feature.login")
class LoginModule
```

```kotlin
// DSL equivalent
val loginModule = module {
    includes(dataModule)
    viewModel<LoginViewModel>()
    factory<LoginUseCase>()
}
```

## Dynamic Feature Loading

Load feature modules on-demand with Activity lifecycle:

```kotlin
class FeatureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        loadKoinModules(featureModule)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(featureModule)
    }
}
```

## Koin vs Hilt Comparison

| Hilt | Koin |
|------|------|
| `@HiltAndroidApp` | `startKoin { androidContext() }` |
| `@InstallIn(SingletonComponent)` | Module loaded in `startKoin {}` |
| `@EntryPoint` for cross-module | Automatic resolution |
| Component dependencies | `includes()` |
| `@ApplicationContext` | `androidContext()` (automatic) |

:::info
**Koin Advantage:** No `@EntryPoint` interfaces needed. Dependencies resolve across modules automatically as long as all modules are loaded.
:::

## Android Testing

### Test Module in Isolation

```kotlin
class LoginViewModelTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            loginModule,
            module {
                single<UserRepository> { mockk() }
                single<AuthService> { mockk() }
            }
        )
    }

    private val viewModel: LoginViewModel by inject()

    @Test
    fun `test login`() {
        // Test with mocked dependencies
    }
}
```

### Verify All Modules

```kotlin
class ModuleCheckTest : KoinTest {

    @Test
    fun `verify all modules`() {
        appModule.verify()  // Verifies included modules too
    }
}
```

## See Also

- **[Modules](/docs/reference/koin-core/modules)** - Core module concepts with `includes()`
- **[Android Module Loading](/docs/reference/koin-android/modules-android)** - Dynamic module loading
- **[Lazy Modules](/docs/reference/koin-core/lazy-modules)** - Background module loading
- **[Testing](/docs/reference/koin-android/instrumented-testing)** - Android testing guide
