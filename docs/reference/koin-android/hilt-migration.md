---
title: Migrating from Hilt
description: Move an Android app from Hilt to Koin step by step, with a mapping of every Hilt concept to its Koin equivalent.
---

This guide moves an Android app from Dagger Hilt to Koin, one step at a time: Gradle, the `Application` class, modules, bindings, ViewModels, entry points, qualifiers, Compose and tests. Each Koin sample comes in the three Koin styles; if you are not sure which one to pick, read [Choosing your style](/docs/intro/choosing-your-style). For a comparison of the two frameworks, see [Koin vs Hilt](/docs/intro/koin-vs-hilt).

:::note
For a full real-world migration, read [Migrating Now in Android to Koin](https://blog.insert-koin.io/migrating-now-in-android-to-koin-annotations-2-3-67d252dbb97d): Google's sample app with 30 Gradle modules, moved from Hilt to Koin Annotations.
:::

## Concept mapping

| Hilt | Compiler Plugin DSL | Annotations | Constructor / Classic DSL |
|------|---------------------|-------------|-------------|
| `@HiltAndroidApp` | `startKoin { }` in `Application.onCreate()` | `@KoinApplication` + `startKoin<T>()` | `startKoin { }` in `Application.onCreate()` |
| `@AndroidEntryPoint` | Not needed: `by inject()`, `by viewModel()` | Not needed | Not needed |
| `@Inject constructor` | `single<T>()` / `factory<T>()` | `@Singleton` / `@Factory` on the class | `singleOf(::T)` / `factoryOf(::T)` |
| `@Module` + `@InstallIn` | `module { }` | `@Module` + `@ComponentScan` | `module { }` |
| `@Provides` | `single { create(::fn) }` | `@Singleton` / `@Factory` on a function | `single { ... }` |
| `@Binds` | `bind Interface::class` | Automatic (supertypes) or `binds = [...]` | `bind Interface::class` |
| `@Singleton` | `single` | `@Singleton` / `@Single` | `single` |
| `@HiltViewModel` | `viewModel<T>()` | `@KoinViewModel` | `viewModelOf(::T)` |
| `@HiltWorker` | `worker<T>()` | `@KoinWorker` | `workerOf(::T)` |
| `@Named`, custom `@Qualifier` | `named("x")`, `@Named` on parameters | `@Named`, custom `@Qualifier` | `named("x")` |
| `@ApplicationContext` | `Context` parameter, provided by `androidContext()` | Same | `androidContext()` |
| `@AssistedInject` | Injected parameters | `@InjectedParam` | Injected parameters |
| `@Inject` field in a class you own | Constructor parameter | Constructor parameter | Constructor parameter |
| `@EntryPoint` (framework class Hilt can't inject) | `KoinComponent` at that entry point only | Same | Same |
| `@HiltAndroidTest` | `KoinTestRule` | `KoinTestRule` | `KoinTestRule` |

### Scope mapping

| Hilt | Koin DSL | Annotations | Lifetime |
|------|----------|-------------|----------|
| `@Singleton` (`SingletonComponent`) | `single` | `@Singleton` | Application |
| `@ActivityRetainedScoped` | `activityRetainedScope { }` | `@ActivityRetainedScope` | Survives configuration changes |
| `@ViewModelScoped` | `viewModelScope { }` | `@ViewModelScope` | ViewModel |
| `@ActivityScoped` | `activityScope { }` | `@ActivityScope` | Activity |
| `@FragmentScoped` | `fragmentScope { }` | `@FragmentScope` | Fragment |

Scope DSL and annotations are explained in [Android Scopes](/docs/reference/koin-android/scope).

## Step 1: Gradle

Remove the Hilt plugin, `hilt-android`, and `hilt-compiler` (kapt or KSP). Then add Koin:

```kotlin
dependencies {
    implementation(platform("io.insert-koin:koin-bom:$koin_version"))
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-androidx-compose") // if you use Compose
}
```

To use the Compiler Plugin DSL or annotations, also apply the [Koin Compiler Plugin](/docs/setup/compiler-plugin). It runs inside the Kotlin compiler and replaces kapt or KSP for Koin. The full list of artifacts is in [Gradle Setup](/docs/setup/gradle).

## Step 2: Application class

Hilt:

```kotlin
@HiltAndroidApp
class MyApplication : Application()
```

Koin:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<MyApplication> {
            androidLogger()
            androidContext(this@MyApplication)
        }
    }
}
```

`@KoinApplication` loads every module tagged `@Configuration`. To list modules explicitly, use `@KoinApplication(modules = [AppModule::class])`.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
```

</TabItem>
</Tabs>

`androidContext()` makes the `Context` available to every definition. See [Starting Koin on Android](/docs/reference/koin-android/start).

## Step 3: Modules and @Provides

Hilt:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ApiService =
        Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .client(client)
            .build()
            .create(ApiService::class.java)
}
```

Koin:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.plugin.module.dsl.create

fun okHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

fun apiService(client: OkHttpClient): ApiService =
    Retrofit.Builder()
        .baseUrl("https://api.example.com")
        .client(client)
        .build()
        .create(ApiService::class.java)

val networkModule = module {
    single { create(::okHttpClient) }
    single { create(::apiService) }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Module
class NetworkModule {

    @Singleton
    fun okHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Singleton
    fun apiService(client: OkHttpClient): ApiService =
        Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .client(client)
            .build()
            .create(ApiService::class.java)
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val networkModule = module {
    single { OkHttpClient.Builder().build() }
    single {
        Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .client(get())
            .build()
            .create(ApiService::class.java)
    }
}
```

</TabItem>
</Tabs>

There is no `@InstallIn`: a definition's lifetime comes from its type (`single`, `factory`, `scoped`), not from the component it is installed in. Classes with an `@Inject constructor` become `single<T>()` / `factory<T>()`, an annotation on the class, or `singleOf(::T)`. More on library objects in [Definitions](/docs/reference/koin-core/definitions#classes-you-dont-own).

## Step 4: Interface bindings

Hilt:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
```

Koin:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val dataModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class UserRepositoryImpl(private val api: ApiService) : UserRepository
```

The class is bound to all its supertypes. To bind only some, use `@Singleton(binds = [UserRepository::class])`.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val dataModule = module {
    singleOf(::UserRepositoryImpl) bind UserRepository::class
}
```

</TabItem>
</Tabs>

## Step 5: ViewModels

Hilt:

```kotlin
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel()
```

Koin:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class UserViewModel(
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel()

val appModule = module {
    viewModel<UserViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@KoinViewModel
class UserViewModel(
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel()
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
class UserViewModel(
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel()

val appModule = module {
    viewModelOf(::UserViewModel)
}
```

</TabItem>
</Tabs>

Koin provides `SavedStateHandle` to ViewModel definitions. In an Activity or Fragment, replace `by viewModels()` with `by viewModel()`. In Compose, replace `hiltViewModel()` with `koinViewModel()`. See [Android ViewModel](/docs/reference/koin-android/viewmodel).

## Step 6: Activities, Fragments and other entry points

Hilt:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var analytics: Analytics
}
```

Koin:

```kotlin
class MainActivity : ComponentActivity() {
    private val analytics: Analytics by inject()
}
```

Remove `@AndroidEntryPoint`. In Activities, Fragments and other Android components, which the framework creates, fields with `@Inject lateinit var` become `by inject()` (lazy) or `get()` (immediate). This is the same in every style. Services, BroadcastReceivers and other components are covered in [Entry Points](/docs/reference/koin-android/entry-points).

In your own classes (repositories, services, use cases, ViewModels), move `@Inject` fields to constructor parameters and declare the class as in [Step 3](#step-3-modules-and-provides). These classes never call `get()` or `by inject()`:

```kotlin
// Hilt: field injection in a class you own
class SyncService @Inject constructor() {
    @Inject lateinit var repository: UserRepository
}

// Koin: constructor injection
class SyncService(private val repository: UserRepository)
```

Hilt's `@EntryPoint` exists for framework classes Hilt can't inject, such as a `ContentProvider` or a library callback. Only at such an entry point, implement [KoinComponent](/docs/reference/koin-core/koin-component) and call `by inject()`. Do not use `KoinComponent` to replace `@Inject` fields in app classes.

## Step 7: Qualifiers

Hilt:

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Provides
@IoDispatcher
fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

class SyncRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
)
```

Koin:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.core.annotation.Named

class SyncRepository(
    @Named("io") private val dispatcher: CoroutineDispatcher,
)

val dispatcherModule = module {
    single<CoroutineDispatcher>(named("io")) { Dispatchers.IO }
    single<SyncRepository>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

Keep your qualifier annotation. Koin reads custom annotations marked with `@Qualifier` (Koin's, `jakarta.inject` or `javax.inject`):

```kotlin
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
class DispatcherModule {
    @Singleton
    @IoDispatcher
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Singleton
class SyncRepository(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
)
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val dispatcherModule = module {
    single<CoroutineDispatcher>(named("io")) { Dispatchers.IO }
    single { SyncRepository(get(named("io"))) }
}
```

</TabItem>
</Tabs>

Type qualifiers (`named<IoDispatcher>()`) and all other options are in [Qualifiers](/docs/reference/koin-core/qualifiers). Hilt's `@ApplicationContext` needs no qualifier: a `Context` parameter receives the context passed to `androidContext()`.

## Step 8: Compose

Hilt needs an entry point to reach a non-ViewModel dependency from a Composable. Koin resolves it directly:

```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel = koinViewModel()) {
    val analytics: Analytics = koinInject()
}
```

See [Koin for Compose](/docs/reference/koin-compose/compose).

## Step 9: Tests

Hilt:

```kotlin
@HiltAndroidTest
class UserRepositoryTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var repository: UserRepository

    @Before
    fun init() = hiltRule.inject()
}
```

Koin:

```kotlin
class UserRepositoryTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appModule, testModule)
    }

    private val repository: UserRepository by inject()
}
```

Hilt's `@TestInstallIn` and `@UninstallModules` become a test module loaded after the production modules. Remove `HiltTestApplication` and the Hilt test runner. See [Unit Testing](/docs/reference/koin-test/testing) and [Android Instrumented Testing](/docs/reference/koin-android/instrumented-testing).

## Migrate gradually with @Inject

With annotations, the Compiler Plugin reads JSR-330 annotations: in a package covered by `@ComponentScan`, a class with an `@Inject constructor` becomes a factory, and a class annotated with `@Singleton` (from `jakarta.inject` or `javax.inject`) a single. You can keep these classes unchanged and migrate Gradle module by Gradle module. See [JSR-330 Annotations](/docs/reference/koin-android/jsr330).

## Multi-module projects

Each Gradle module declares its own Koin module, and the app module loads them all in `startKoin`. There is no component hierarchy to configure and no `@EntryPoint` for cross-module access: a definition in `:feature:home` can use any definition loaded by the app.

With annotations, tag each feature module class with `@Module`, `@ComponentScan` and `@Configuration`. `@KoinApplication` in the app module finds them without listing them.

Module organization, visibility and feature modules are covered in [Multi-Module Apps](/docs/reference/koin-android/multi-module).

## Common patterns

### Assisted injection

Hilt's `@AssistedInject` and `@AssistedFactory` become injected parameters. The caller passes the runtime value:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class UserDetailViewModel(
    @InjectedParam private val userId: String,
    private val repository: UserRepository,
) : ViewModel()

val appModule = module {
    viewModel<UserDetailViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@KoinViewModel
class UserDetailViewModel(
    @InjectedParam private val userId: String,
    private val repository: UserRepository,
) : ViewModel()
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
class UserDetailViewModel(
    private val userId: String,
    private val repository: UserRepository,
) : ViewModel()

val appModule = module {
    viewModel { (userId: String) -> UserDetailViewModel(userId, get()) }
}
```

</TabItem>
</Tabs>

```kotlin
val viewModel: UserDetailViewModel by viewModel { parametersOf("user-42") }
```

See [Injected Parameters](/docs/reference/koin-core/injection-parameters).

### Lazy and Provider

- `dagger.Lazy<T>`: use a Kotlin `Lazy<T>` constructor parameter, or `by inject()` in an Android entry point (see [Injection](/docs/reference/koin-core/injection)).
- `Provider<T>`: to create a new instance on each call from a class you own, use the provider pattern in [Advanced Patterns](/docs/reference/koin-core/advanced-patterns).

### WorkManager

`@HiltWorker` workers become `worker` definitions, and `HiltWorkerFactory` becomes `workManagerFactory()` in `startKoin`. See [WorkManager](/docs/reference/koin-android/workmanager).

## Checklist

- [ ] Gradle: remove the Hilt plugin, `hilt-android` and `hilt-compiler`. Add Koin, and the Compiler Plugin if you use it. Remove kapt if nothing else needs it.
- [ ] Application: remove `@HiltAndroidApp`, start Koin in `onCreate()` with `androidContext()`.
- [ ] Modules: convert `@Module` / `@InstallIn` / `@Provides` / `@Binds` to Koin modules.
- [ ] ViewModels: remove `@HiltViewModel`, declare them, use `by viewModel()` / `koinViewModel()`.
- [ ] Entry points: remove `@AndroidEntryPoint`, use `by inject()` in Android components. Move `@Inject` fields of your own classes to constructor parameters. Replace `@EntryPoint` with `KoinComponent` only in framework classes Koin can't create.
- [ ] Qualifiers: keep custom `@Qualifier` annotations (annotations style) or switch to `named()`.
- [ ] Workers: replace `@HiltWorker` and `HiltWorkerFactory`.
- [ ] Tests: remove `@HiltAndroidTest`, `HiltAndroidRule` and the Hilt test runner, use `KoinTestRule`.
- [ ] Build: with the Compiler Plugin, a successful build means the graph is complete. Without it, add a [verify()](/docs/reference/koin-test/verify) test.
- [ ] Run: launch the app and the test suite.

## Troubleshooting

### NoDefinitionFoundException

Koin has no definition for the requested type. Check that the module declaring it is loaded in `startKoin` (or found by `@Configuration`), and that the requested type matches the declared or bound type. A class that only had `@Inject constructor` and was injected by Hilt needs a Koin definition too, unless a `@ComponentScan` picks it up through its JSR-330 annotations.

### Wrong instance with two definitions of the same type

With Hilt, a missing qualifier is a build error. With Koin, the last loaded definition wins, unless you start Koin with `allowOverride(false)`. Add a [qualifier](/docs/reference/koin-core/qualifiers) to both definitions and to the injection points.

### Circular dependency

Two classes depend on each other. Refactor to break the cycle, or make one side lazy with a `Lazy<T>` parameter. The Compiler Plugin reports cycles at build time. More in [Troubleshooting](/docs/reference/koin-core/troubleshooting).

## Related

- [Koin vs Hilt](/docs/intro/koin-vs-hilt): how the two frameworks differ.
- [JSR-330 Annotations](/docs/reference/koin-android/jsr330): keep `@Inject` and `@Singleton` during the migration.
- [Starting Koin on Android](/docs/reference/koin-android/start): `startKoin`, `androidContext()` and logging.
- [Android Scopes](/docs/reference/koin-android/scope): Activity, Fragment and ViewModel scopes.
- [Unit Testing](/docs/reference/koin-test/testing): replace Hilt test rules.
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): what the Compiler Plugin checks.
