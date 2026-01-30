---
title: Migrating from Hilt to Koin
---

This guide helps you migrate your Android application from Dagger Hilt to Koin. Whether you're using Koin DSL or Koin Annotations, this guide covers the key differences and migration steps.

:::info
For a complete real-world example, check out the [Now in Android migration](https://blog.insert-koin.io/migrating-now-in-android-to-koin-annotations-2-3-67d252dbb97d) which shows how Google's production-ready news app with 30 Gradle modules was migrated from Hilt to Koin Annotations.
:::

## Why Migrate to Koin?

**Key advantages of Koin:**

- **No Code Generation** - Koin uses runtime dependency resolution without annotation processors
- **Simpler Setup** - No complex component hierarchies or `@InstallIn` declarations
- **Kotlin-First** - Idiomatic Kotlin DSL that feels natural
- **Lighter Weight** - Faster build times without kapt/KSP code generation (for DSL approach)
- **Multi-Module Friendly** - No `@EntryPoint` interfaces needed
- **JSR-330 Support** - Existing `@Inject` constructors work without modification

## Quick Reference: Hilt vs Koin

### Annotation Mappings

| Hilt | Koin DSL                                 | Koin Annotations                                                                                    |
|------|------------------------------------------|-----------------------------------------------------------------------------------------------------|
| `@HiltAndroidApp` | `startKoin {}` in Application            | `@KoinApplication`                                                                                  |
| `@AndroidEntryPoint` | `by inject()` / `by viewModel()`         | `by inject()` / `by viewModel()`                                                                    |
| `@HiltViewModel` | `viewModel { MyViewModel(...) }`         | `@KoinViewModel`                                                                                    |
| `@Inject` constructor | DSL to specify constructor parameters    | Constructor parameters detected (JSR-330)                                                           |
| `@Module` + `@InstallIn` | `module { }`                             | `@Module` + `@ComponentScan`                                                                        |
| `@Provides` | `single { }` or `factory { }`            | `@Single` / `@Factory`                                                                              |
| `@Binds` | `single<Interface> { Implementation() }` | `@Single` or `@Singleton` are detecting bindings. Also use `binds` property from those annotations. |
| `@Singleton` | `single { }`                             | `@Single`or `@Singleton`                                                                                           |
| `@Named("qualifier")` | `named("qualifier")`                     | `@Named("qualifier")`                                                                               |
| `@ApplicationContext` | Automatic context injection              | Automatic context injection                                                                         |
| `@EntryPoint` | Not needed                               | Not needed                                                                                          |

### Scope Mappings

| Hilt Scope | Koin DSL | Koin Annotations | Notes |
|------------|----------|------------------|-------|
| `@Singleton` | `single { }` | `@Single` / `@Singleton` | Application-wide singleton |
| `@ActivityScoped` | `activityScope { scoped { } }` | `@ActivityScope` | Tied to Activity lifecycle |
| `@ViewModelScoped` | `viewModelScope { scoped { } }` | `@ViewModelScope` | Tied to ViewModel lifecycle |
| `@ActivityRetainedScoped` | `activityRetainedScope { scoped { } }` | `@ActivityRetainedScope` | Survives configuration changes |

## Migration Steps

### Step 1: Update Dependencies

**Remove Hilt dependencies:**

```kotlin
// Remove these from build.gradle.kts
plugins {
    id("com.google.dagger.hilt.android") // Remove
}

dependencies {
    // Remove Hilt dependencies
    implementation("com.google.dagger:hilt-android:...")
    kapt("com.google.dagger:hilt-compiler:...")
}
```

**Add Koin dependencies:**

```kotlin
// build.gradle.kts (app module)
dependencies {
    // Koin for Android
    implementation("io.insert-koin:koin-android:$koin_version")
    implementation("io.insert-koin:koin-androidx-compose:$koin_version")

    // Optional: Koin Annotations
    implementation("io.insert-koin:koin-annotations:$koin_ksp_version")
    ksp("io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
}
```

### Step 2: Application Setup

**Hilt:**

```kotlin
@HiltAndroidApp
class MyApplication : Application()
```

**Koin DSL:**

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule, dataModule, domainModule)
        }
    }
}
```

**Koin Annotations:**

```kotlin
@KoinApplication
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
        }
    }
}
```

:::info
With `@KoinApplication`, modules are automatically discovered if they're tagged with `@Configuration`. You can also explicitly include modules using the `modules` property: `@KoinApplication(modules = [AppModule::class])`.
:::

### Step 3: Migrate Modules

**Hilt:**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

**Koin DSL:**

```kotlin
val networkModule = module {

    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .client(get()) // Automatic dependency resolution
            .build()
    }

    single {
        get<Retrofit>().create(ApiService::class.java)
    }
}
```

**Koin Annotations:**

```kotlin
@Module
class NetworkModule {

    @Single
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Single
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .client(okHttpClient)
            .build()
    }

    @Single
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

### Step 4: Migrate ViewModels

**Hilt:**

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // ...
}

@Composable
fun MyScreen() {
    val viewModel = hiltViewModel<MyViewModel>()
    // ...
}
```

**Koin DSL:**

```kotlin
class MyViewModel(
    private val repository: MyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // ...
}

val appModule = module {
    viewModelOf(::MyViewModel)
}

@Composable
fun MyScreen() {
    val viewModel = koinViewModel<MyViewModel>()
    // ...
}
```

**Koin Annotations:**

```kotlin
@KoinViewModel
class MyViewModel(
    private val repository: MyRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // ...
}

@Composable
fun MyScreen() {
    val viewModel = koinViewModel<MyViewModel>()
    // ...
}
```

:::info
The `viewModelOf` DSL function uses constructor parameter autowiring. `SavedStateHandle` is automatically provided by Koin, so you don't need to explicitly pass it. This is part of Koin's autowire DSL which simplifies ViewModel definitions.
:::

### Step 5: Migrate Activities and Fragments

**Hilt:**

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analytics: AnalyticsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics.logEvent("screen_view")
    }
}
```

**Koin:**

```kotlin
class MainActivity : ComponentActivity() {

    // Property delegation - no annotation needed
    private val analytics: AnalyticsService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics.logEvent("screen_view")
    }
}
```

:::info
With Koin, you don't need `@AndroidEntryPoint` - just use `by inject()` or `by viewModel()` property delegation.
:::

### Step 6: Migrate Interface Bindings

**Hilt:**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindRepository(
        impl: MyRepositoryImpl
    ): MyRepository
}

class MyRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MyRepository {
    // ...
}
```

**Koin DSL:**

```kotlin
val dataModule = module {
    single<MyRepository> { MyRepositoryImpl(get()) }
}

class MyRepositoryImpl(
    private val apiService: ApiService
) : MyRepository {
    // ...
}
```

**Koin Annotations (Automatic Binding Detection):**

```kotlin
// Option 1: Automatic - Koin detects the interface binding
@Singleton
class MyRepositoryImpl(
    private val apiService: ApiService
) : MyRepository {
    // ...
}
// Koin automatically binds MyRepositoryImpl to MyRepository

// Option 2: Explicit with binds property
@Single(binds = [MyRepository::class])
class MyRepositoryImpl(
    private val apiService: ApiService
) : MyRepository {
    // ...
}
```

:::info
Koin Annotations automatically detects interface bindings when a class implements an interface. Use the `binds` property when you need to explicitly specify multiple interfaces or control binding behavior.
:::

### Step 7: Migrate Qualifiers

**Hilt:**

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}

class MyRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher
)
```

**Koin DSL (String-based):**

```kotlin
val dispatcherModule = module {
    single(named("io")) { Dispatchers.IO }
}

class MyRepository(
    private val dispatcher: CoroutineDispatcher
)

val dataModule = module {
    single { MyRepository(get(named("io"))) }
}
```

**Koin DSL (Type-safe):**

```kotlin
// Define a qualifier type
object IoDispatcher

val dispatcherModule = module {
    single(named<IoDispatcher>()) { Dispatchers.IO }
}

class MyRepository(
    private val dispatcher: CoroutineDispatcher
)

val dataModule = module {
    single { MyRepository(get(named<IoDispatcher>())) }
}
```

**Koin Annotations (String-based):**

```kotlin
@Module
class DispatcherModule {
    @Single
    @Named("io")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Single
class MyRepository(
    @InjectedParam @Named("io") private val dispatcher: CoroutineDispatcher
)
```

**Koin Annotations (With JSR-330 @Qualifier - Fully Compatible!):**

```kotlin
// Keep your existing JSR-330 qualifier annotation!
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
class DispatcherModule {
    @Single
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Single
class MyRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher
)
```

:::info
Koin Annotations fully supports JSR-330 `@Qualifier` annotations! This is a standard Java/Kotlin DI annotation (not Hilt-specific), so you can keep your existing qualifier annotations unchanged during migration. The DSL also supports type-safe qualifiers using `named<T>()` instead of string-based `named("string")`.
:::

### Step 8: Migrate Compose Integration

**Hilt:**

```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel()
) {
    val dependency: SomeDependency = EntryPointAccessors
        .fromActivity<MyEntryPoint>(LocalContext.current as Activity)
        .dependency()
}
```

**Koin:**

```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = koinViewModel()
) {
    // Direct injection - no EntryPoint needed
    val dependency: SomeDependency = koinInject()
}
```

### Step 9: Migrate Testing

**Hilt:**

```kotlin
@HiltAndroidTest
class MyTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: MyRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun myTest() {
        // ...
    }
}
```

**Koin:**

```kotlin
class MyTest : KoinTest {

    private val repository: MyRepository by inject()

    @Before
    fun before() {
        startKoin {
            modules(testModule)
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun myTest() {
        // ...
    }
}
```

## Multi-Module Projects

### Hilt Approach

With Hilt, you need:
- `@InstallIn` to specify component hierarchy
- `@EntryPoint` interfaces for cross-module access
- Complex component dependencies

### Koin Approach

With Koin:
- Each module declares its own Koin module
- Import all modules in your Application class
- No special interfaces needed

**Feature module with Koin:**

```kotlin
// :feature:home module
val homeModule = module {
    viewModel { HomeViewModel(get()) }
    factory { HomeRepository(get()) }
}

// :app module
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                coreModule,
                dataModule,
                homeModule,  // Feature module
                profileModule // Another feature module
            )
        }
    }
}
```

See [Multi-Module Architecture](/docs/reference/koin-android/multi-module) for more details.

## Common Patterns

### Constructor Injection (JSR-330)

One of the biggest advantages: **existing `@Inject` constructors work with Koin Annotations!**

```kotlin
// This works with both Hilt and Koin Annotations
class MyRepository @Inject constructor(
    private val apiService: ApiService,
    private val database: AppDatabase
) {
    // ...
}
```

With Koin Annotations, you can keep your `@Inject` constructors unchanged and just add `@Single`, `@Singleton`, or `@Factory` to the class:

```kotlin
@Single // or @Singleton
class MyRepository @Inject constructor(
    private val apiService: ApiService,
    private val database: AppDatabase
) {
    // ...
}
```

### AssistedInject

**Hilt:**

```kotlin
class MyViewModel @AssistedInject constructor(
    private val repository: MyRepository,
    @Assisted private val userId: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(userId: String): MyViewModel
    }
}
```

**Koin:**

```kotlin
class MyViewModel(
    private val repository: MyRepository,
    private val userId: String
) : ViewModel()

val appModule = module {
    viewModelOf(::MyViewModel)
}

// Usage
val viewModel: MyViewModel by viewModel { parametersOf("user123") }
```

### Lazy Injection

**Hilt:**

```kotlin
@Inject
lateinit var heavyService: HeavyService
```

**Koin:**

```kotlin
// Lazy by default with property delegation
private val heavyService: HeavyService by inject()

// Or explicit lazy
private val heavyService: Lazy<HeavyService> by lazy { get() }
```

## Migration Checklist

Use this checklist to track your migration progress:

- [ ] **Dependencies**
  - [ ] Remove Hilt Gradle plugin
  - [ ] Remove Hilt dependencies
  - [ ] Add Koin dependencies
  - [ ] Remove `kapt` if not needed elsewhere

- [ ] **Application Class**
  - [ ] Remove `@HiltAndroidApp`
  - [ ] Add `startKoin {}` in `onCreate()`
  - [ ] Configure `androidContext()` and modules

- [ ] **Modules**
  - [ ] Convert `@Module` + `@InstallIn` to `module { }`
  - [ ] Convert `@Provides` to `single { }` or `factory { }`
  - [ ] Convert `@Binds` to interface bindings
  - [ ] Update qualifiers to `named()`

- [ ] **ViewModels**
  - [ ] Remove `@HiltViewModel`
  - [ ] Add to module with `viewModel { }`
  - [ ] Update Composables to use `koinViewModel()`

- [ ] **Activities/Fragments**
  - [ ] Remove `@AndroidEntryPoint`
  - [ ] Convert field injection to `by inject()`

- [ ] **Testing**
  - [ ] Remove `@HiltAndroidTest`
  - [ ] Implement `KoinTest`
  - [ ] Add `startKoin` / `stopKoin` in setup/teardown

- [ ] **Verification**
  - [ ] Build project successfully
  - [ ] Run all tests
  - [ ] Test in-app dependency injection
  - [ ] Verify no runtime crashes

## Troubleshooting

### "No definition found for X"

**Issue**: Koin can't find a definition for a type.

**Solution**:
- Ensure the module is loaded in `startKoin { modules(...) }`
- Check that the definition exists (use `single { }` or `factory { }`)
- Verify the correct type is specified

### "DefinitionOverrideException"

**Issue**: Multiple definitions for the same type.

**Solution**:
- Use qualifiers: `single(named("qualifier")) { }`
- Enable override: `startKoin { allowOverride(true) }`

### Circular Dependencies

**Issue**: Two classes depend on each other.

**Solution**:
- Use `lazy` injection: `private val service by lazy { get<MyService>() }`
- Refactor to remove circular dependency
- Use scopes to break the cycle

## Additional Resources

- **Real-World Migration**: [Migrating Now in Android to Koin](https://blog.insert-koin.io/migrating-now-in-android-to-koin-annotations-2-3-67d252dbb97d)
- **Koin Documentation**: [Getting Started](/docs/setup/koin)
- **Koin Annotations**: [Android Annotations Guide](/docs/quickstart/android-annotations)

## Need Help?

- **GitHub Discussions**: Ask questions in the [Koin repository](https://github.com/InsertKoinIO/koin/discussions)
- **Slack**: Join the Koin community on Slack
- **Stack Overflow**: Tag questions with `koin`