---
title: Retrieving Dependencies
---

# Retrieving Dependencies

This guide covers how to retrieve dependencies from Koin in different contexts.

## Approaches

| Approach | When to Use | Example |
|----------|-------------|---------|
| **Constructor Injection** | Business logic, services, repositories | `class MyService(val repo: Repository)` |
| **Function Injection** | Factory functions, builders | `fun createHttpClient(val ds: DataSource): HttpClient` |
| **Field Injection** | Android framework classes, entry points | `val viewModel: MyViewModel by viewModel()` |

:::info
**Best Practice:** Prefer constructor or function injection for better testability. Use field injection only when you don't control the class construction (Activities, Fragments, etc.).
:::

## Constructor Injection (Recommended)

Dependencies are declared in the constructor and resolved by Koin:

```kotlin
class UserRepository(
    private val database: Database,
    private val apiClient: ApiClient
)

class UserViewModel(
    private val repository: UserRepository
) : ViewModel()
```

```kotlin
val appModule = module {
    single<Database>()
    single<ApiClient>()
    single<UserRepository>()
    viewModel<UserViewModel>()
}
```

Koin automatically resolves all constructor parameters.

## Function Injection

Use functions to create instances when you need custom creation logic:

### Compiler Plugin DSL

```kotlin
fun createHttpClient(dataSource: DataSource): HttpClient {
    return HttpClient {
        install(ContentNegotiation) { json() }
        defaultRequest { url(dataSource.baseUrl) }
    }
}

val appModule = module {
    single<DataSource>()
    single { create(::createHttpClient) }
}
```

### Annotations

```kotlin
@Module
class NetworkModule {

    @Singleton
    fun createHttpClient(dataSource: DataSource): HttpClient {
        return HttpClient {
            install(ContentNegotiation) { json() }
            defaultRequest { url(dataSource.baseUrl) }
        }
    }
}
```

Function injection is useful when:
- Creating instances from external libraries you don't control
- Complex initialization logic is needed
- You need to configure builders or DSLs

## Field Injection

### Lazy Injection with `by inject()`

Creates the instance when first accessed:

```kotlin
class MyActivity : AppCompatActivity() {
    // Lazy - created on first access
    private val viewModel: UserViewModel by viewModel()
    private val service: MyService by inject()
}
```

### Eager Injection with `get()`

Creates the instance immediately:

```kotlin
class MyActivity : AppCompatActivity() {
    // Eager - created immediately
    private val service: MyService = get()
}
```

### Comparison

| Method | When Created | Thread Safety |
|--------|--------------|---------------|
| `by inject()` | On first access | Thread-safe lazy |
| `get()` | Immediately | Direct call |

## KoinComponent

For classes that need to inject dependencies but aren't Android components:

```kotlin
class MyHelper : KoinComponent {
    private val service: MyService by inject()
    private val database: Database = get()

    fun doSomething() {
        service.process(database.query())
    }
}
```

:::warning
Avoid using `KoinComponent` in business logic classes. It creates tight coupling to Koin. Prefer constructor injection instead.
:::

## Platform-Specific Injection

### Android

Activities and Fragments have built-in support:

```kotlin
class MainActivity : AppCompatActivity() {
    // ViewModel injection
    private val viewModel: UserViewModel by viewModel()

    // Regular injection
    private val analytics: AnalyticsService by inject()
}

class UserFragment : Fragment() {
    // Fragment's own ViewModel
    private val viewModel: UserViewModel by viewModel()

    // Shared with Activity
    private val sharedVM: SharedViewModel by activityViewModels()
}
```

### Compose

```kotlin
@Composable
fun UserScreen() {
    // Inject ViewModel
    val viewModel: UserViewModel = koinViewModel()

    // Inject any dependency
    val analytics: AnalyticsService = koinInject()

    // Activity-scoped ViewModel
    val sharedVM: SharedViewModel = koinActivityViewModel()
}
```

### Ktor

```kotlin
fun Route.userRoutes() {
    val repository: UserRepository by inject()

    get("/users") {
        call.respond(repository.getAll())
    }
}
```

## Injection with Qualifiers

When you have multiple definitions of the same type, use qualifiers to distinguish them.

### String Qualifier

| DSL | Annotation |
|-----|------------|
| `named("local")` | `@Named("local")` |

```kotlin
// DSL
val module = module {
    single<Database>(named("local")) { LocalDatabase() }
    single<Database>(named("remote")) { RemoteDatabase() }
}

// Injection
private val localDb: Database by inject(named("local"))
private val remoteDb: Database by inject(named("remote"))
```

```kotlin
// Annotations
@Singleton
@Named("local")
class LocalDatabase : Database

@Singleton
@Named("remote")
class RemoteDatabase : Database
```

### Type Qualifier

Use a type (class, object, or enum) as a qualifier for compile-time safety:

| DSL | Annotation |
|-----|------------|
| `named<LocalDb>()` | `@Qualifier(LocalDb::class)` |

```kotlin
// Define qualifier types
object LocalDb
object RemoteDb

// DSL
val module = module {
    single<Database>(named<LocalDb>()) { LocalDatabase() }
    single<Database>(named<RemoteDb>()) { RemoteDatabase() }
}

// Injection
private val localDb: Database by inject(named<LocalDb>())
private val remoteDb: Database by inject(named<RemoteDb>())
```

```kotlin
// Annotations
@Singleton
@Qualifier(LocalDb::class)
class LocalDatabase : Database

@Singleton
@Qualifier(RemoteDb::class)
class RemoteDatabase : Database
```

### In Compose

```kotlin
@Composable
fun MyScreen() {
    // With string qualifier
    val localDb: Database = koinInject(named("local"))

    // With type qualifier
    val remoteDb: Database = koinInject(named<RemoteDb>())
}
```

## Injection with Parameters

Pass parameters at injection time:

### Definition

```kotlin
@Factory
class UserPresenter(
    @InjectedParam val userId: String,
    val repository: UserRepository
)

// Or with DSL
factory<UserPresenter>()
```

### Injection

```kotlin
// by inject()
private val presenter: UserPresenter by inject { parametersOf("user123") }

// get()
val presenter: UserPresenter = get { parametersOf("user123") }
```

### In Compose

```kotlin
@Composable
fun UserScreen(userId: String) {
    val presenter: UserPresenter = koinInject { parametersOf(userId) }
}
```

### Multiple Parameters

```kotlin
@Factory
class OrderPresenter(
    @InjectedParam val userId: String,
    @InjectedParam val orderId: String,
    val repository: OrderRepository
)

val presenter = get<OrderPresenter> { parametersOf("user123", "order456") }
```

## Direct Koin Access

Access the Koin instance directly when needed:

```kotlin
// From GlobalContext
val koin = KoinPlatform.getKoin()
val service: MyService = koin.get()

// In KoinComponent
class MyClass : KoinComponent {
    fun doSomething() {
        val service: MyService = getKoin().get()
    }
}
```

## Nullable Injection

For optional dependencies:

```kotlin
// Returns null if not found
val optional: MyService? = getKoinOrNull()?.getOrNull()

// In KoinComponent
class MyClass : KoinComponent {
    private val optional: MyService? = getOrNull()
}
```

## Injection in Different Contexts

### In ViewModel

```kotlin
class UserViewModel(
    private val repository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // Constructor injection - no KoinComponent needed
}
```

### In Service

```kotlin
class MyService : Service() {
    private val repository: UserRepository by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        repository.doSomething()
        return START_STICKY
    }
}
```

### In BroadcastReceiver

```kotlin
class MyReceiver : BroadcastReceiver(), KoinComponent {
    private val service: NotificationService by inject()

    override fun onReceive(context: Context, intent: Intent) {
        service.handleNotification(intent)
    }
}
```

### In WorkManager Worker

```kotlin
class MyWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: UserRepository  // Injected by Koin
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        repository.syncData()
        return Result.success()
    }
}

// Module
val workerModule = module {
    worker<MyWorker>()
}
```

## Best Practices

### DO: Constructor Injection for Business Logic

```kotlin
// Good - testable without Koin
class UserService(
    private val repository: UserRepository,
    private val validator: UserValidator
) {
    fun createUser(data: UserData) = validator.validate(data).let {
        repository.save(it)
    }
}

// Test without Koin
@Test
fun testCreateUser() {
    val mockRepo = mockk<UserRepository>()
    val mockValidator = mockk<UserValidator>()
    val service = UserService(mockRepo, mockValidator)
    // Test directly
}
```

### DO: Field Injection for Framework Classes

```kotlin
// Good - Activity construction is controlled by Android
class MainActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModel()
}
```

### DON'T: KoinComponent in Business Logic

```kotlin
// Bad - tight coupling to Koin
class UserService : KoinComponent {
    private val repository: UserRepository by inject()
}

// Good - constructor injection
class UserService(private val repository: UserRepository)
```

### DON'T: Get in Constructors

```kotlin
// Bad - side effects in constructor
class MyService(
    private val repo: UserRepository = get()  // Don't do this!
)

// Good - let Koin inject
class MyService(private val repo: UserRepository)
```

## Next Steps

- **[Scopes](/docs/reference/koin-core/scopes)** - Manage dependency lifecycle
- **[Koin for Android](/docs/integrations/android/index)** - Android-specific injection
- **[Koin for Compose](/docs/integrations/compose/index)** - Compose injection
