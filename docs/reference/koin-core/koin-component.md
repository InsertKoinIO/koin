---
title: Koin Component
---

`KoinComponent` provides an API to retrieve instances from the Koin container outside of module definitions. This is useful for classes that cannot use constructor injection, like Android Activities or framework classes.

## What is KoinComponent?

`KoinComponent` is an interface that gives any class access to the Koin container API. It provides functions to retrieve instances without requiring constructor injection.

:::info
**Prefer constructor injection when possible.** Only use `KoinComponent` for classes you can't declare in modules (framework classes, UI components, etc.). Constructor injection is clearer, more testable, and doesn't couple your code to Koin.
:::

## Basic Usage

### Creating a Koin Component

Tag your class with the `KoinComponent` interface:

```kotlin
class MyService

val myModule = module {
    single { MyService() }
}
```

```kotlin
// Start Koin
fun main() {
    startKoin {
        modules(myModule)
    }

    // Create component that uses Koin
    MyComponent()
}
```

```kotlin
class MyComponent : KoinComponent {
    // Lazy evaluation - instance retrieved when first accessed
    val myService: MyService by inject()

    // or

    // Eager evaluation - instance retrieved immediately
    val myService: MyService = get()
}
```

### Available Functions

Once you implement `KoinComponent`, you gain access to:

| Function | Description |
|----------|-------------|
| `get<T>()` | Eagerly retrieve an instance |
| `by inject<T>()` | Lazily retrieve an instance (delegate) |
| `getProperty()` | Get configuration property |
| `setProperty()` | Set configuration property |
| `getKoin()` | Access the Koin instance |

## Retrieving Instances

### Eager vs Lazy Retrieval

**Eager with `get()`:**
```kotlin
class MyComponent : KoinComponent {
    // Instance retrieved immediately during construction
    val service: MyService = get()

    init {
        service.doSomething()  // Already available
    }
}
```

**Lazy with `by inject()`:**
```kotlin
class MyComponent : KoinComponent {
    // Instance retrieved only when first accessed
    val service: MyService by inject()

    fun doWork() {
        service.doSomething()  // Retrieved here on first access
    }
}
```

:::note
Use `by inject()` for properties that may not always be needed. It defers instance creation until first access.
:::

### When to Use Each

| Use `get()` | Use `by inject()` |
|-------------|-------------------|
| Need instance immediately in `init` | Property may not be used |
| Simple, direct access | Want lazy initialization |
| Building objects in functions | Declaring class properties |

## Qualifiers

Retrieve named definitions using qualifiers:

```kotlin
module {
    single(named("local")) { LocalDatabase() }
    single(named("remote")) { RemoteDatabase() }
}
```

```kotlin
class DataManager : KoinComponent {
    val localDb: Database = get(named("local"))
    val remoteDb: Database = get(named("remote"))

    // or lazy
    val localDb: Database by inject(named("local"))
}
```

### Qualifier Types

**String qualifiers:**
```kotlin
val service = get<ApiService>(named("production"))
```

**Type qualifiers:**
```kotlin
val service = get<ApiService>(named<ProductionService>())
```

**Enum qualifiers:**
```kotlin
enum class Environment { DEV, PROD }

val service = get<ApiService>(named(Environment.PROD))
```

## Injection Parameters

Pass runtime parameters when retrieving instances:

```kotlin
module {
    factory { (userId: String, sessionId: String) ->
        UserSession(userId, sessionId)
    }
}
```

```kotlin
class LoginController : KoinComponent {
    fun login(userId: String) {
        val session: UserSession = get { parametersOf(userId, "session-123") }
        session.start()
    }
}
```

**With lazy injection:**
```kotlin
class ProfileScreen : KoinComponent {
    private val userId = "user-456"

    // Parameters evaluated when first accessed
    val userSession: UserSession by inject { parametersOf(userId, "profile-session") }
}
```

## Properties

Access and modify Koin properties from components:

### Get Properties

```kotlin
class ApiClientFactory : KoinComponent {
    val apiUrl: String = getProperty("server_url")
    val apiKey: String = getProperty("api_key", "default-key")  // With default
    val timeout: Int = getProperty("timeout", "30").toInt()
}
```

### Set Properties

```kotlin
class ConfigManager : KoinComponent {
    fun updateServerUrl(url: String) {
        setProperty("server_url", url)
    }

    fun enableDebugMode() {
        setProperty("debug_mode", "true")
    }
}
```

### Property Lifecycle

Properties set with `setProperty()` are:
- Available to all components
- Persist for the Koin instance lifetime
- Reset when `stopKoin()` is called

## Accessing the Koin Instance

Get direct access to the `Koin` container:

```kotlin
class AdvancedComponent : KoinComponent {
    fun performComplexOperation() {
        val koin = getKoin()

        // Access scopes
        val scope = koin.createScope<MyActivity>()

        // Check if definition exists
        val hasService = koin.getOrNull<MyService>() != null

        // Get all instances of a type
        val allServices = koin.getAll<Service>()
    }
}
```

## Real-World Examples

### Android Activity (Recommended Approach)

:::info
Android Activities **don't need KoinComponent**. Use Koin Android extensions:
:::

```kotlin
// ✅ Preferred - No KoinComponent needed
class MainActivity : AppCompatActivity() {
    private val userRepository: UserRepository by inject()
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dependencies available via Koin Android extensions
        val user = userRepository.getCurrentUser()
    }
}
```

For comparison, the old way (not recommended):
```kotlin
// ❌ Not needed - KoinComponent is redundant for Activities
class MainActivity : AppCompatActivity(), KoinComponent {
    private val userRepository: UserRepository by inject()
    // ...
}
```

### Android Fragment (Recommended Approach)

```kotlin
// ✅ Preferred - No KoinComponent needed
class UserFragment : Fragment() {
    private val viewModel: UserViewModel by viewModel()
    private val userRepository: UserRepository by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadUser()
    }
}
```

### Custom Android View (KoinComponent needed)

```kotlin
// ✅ KoinComponent is appropriate here
class CustomChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), KoinComponent {

    private val dataFormatter: ChartDataFormatter by inject()
    private val colorScheme: ColorScheme by inject()

    fun setData(data: List<DataPoint>) {
        val formatted = dataFormatter.format(data)
        // Render chart...
    }
}
```

### Console Application

```kotlin
class ConsoleApp : KoinComponent {
    private val logger: Logger by inject()
    private val dataProcessor: DataProcessor by inject()

    fun run() {
        logger.info("Starting application")
        dataProcessor.process()
        logger.info("Application finished")
    }
}

fun main() {
    startKoin {
        modules(appModule)
    }

    ConsoleApp().run()

    stopKoin()
}
```

### Kotlin Multiplatform Common Code

```kotlin
// Common code that works across platforms
class FeatureManager : KoinComponent {
    private val api: ApiClient by inject()
    private val cache: Cache by inject()

    suspend fun loadFeatures(): List<Feature> {
        return cache.get() ?: api.fetchFeatures().also { cache.set(it) }
    }
}
```

## When to Use KoinComponent

### Good Use Cases

Use `KoinComponent` for:

- **Custom UI components** - Custom Views, Widgets (not Activities/Fragments - see note below)
- **Entry points** - Main functions, Application classes
- **Callbacks** - Listeners, handlers that can't use constructor injection
- **Legacy code** - Classes you can't refactor to use DI
- **Non-Android platforms** - JVM, Native, JS applications

:::note
**Android developers:** Activities, Fragments, and Services have dedicated Koin extensions and **don't need KoinComponent**. Use `by inject()` and `by viewModel()` directly without implementing the interface. See [Android Injection](/docs/reference/koin-android/get-instances) for details.
:::

### Avoid Using For

Don't use `KoinComponent` for:

- **Android Activities/Fragments/Services** - Use Koin Android extensions instead
- **Business logic** - Use constructor injection instead
- **Data layer** - Repositories, data sources
- **Domain layer** - Use cases, domain models
- **New code** - Prefer declaring in modules with constructor injection

## Best Practices

### Prefer Constructor Injection

```kotlin
// ❌ Avoid - KoinComponent for business logic
class UserService : KoinComponent {
    private val repository: UserRepository by inject()
    private val validator: UserValidator by inject()

    fun createUser(name: String) { /* ... */ }
}

// ✅ Prefer - Constructor injection
class UserService(
    private val repository: UserRepository,
    private val validator: UserValidator
) {
    fun createUser(name: String) { /* ... */ }
}

// Declare in module
module {
    single { UserRepository() }
    single { UserValidator() }
    single { UserService(get(), get()) }
}
```

### Use Lazy Injection for Optional Dependencies

```kotlin
class FeatureController : KoinComponent {
    // May not be used in all code paths
    private val analyticsService: AnalyticsService by inject()

    fun performAction(trackAnalytics: Boolean) {
        doWork()

        // Analytics only retrieved if needed
        if (trackAnalytics) {
            analyticsService.track("action_performed")
        }
    }
}
```

### Limit KoinComponent Scope

```kotlin
// ❌ Bad - Too many components using Koin directly
class RepositoryA : KoinComponent {
    val db: Database by inject()
}

class RepositoryB : KoinComponent {
    val db: Database by inject()
}

class ServiceA : KoinComponent {
    val repoA: RepositoryA by inject()
}

// ✅ Better - Single entry point, rest uses constructor injection
class AppController : KoinComponent {
    private val serviceA: ServiceA = get()
    private val serviceB: ServiceB = get()
}

class ServiceA(private val repoA: RepositoryA)
class ServiceB(private val repoB: RepositoryB)
class RepositoryA(private val db: Database)
class RepositoryB(private val db: Database)
```

### Don't Overuse getProperty

```kotlin
// ❌ Avoid - Retrieving properties everywhere
class FeatureA : KoinComponent {
    val apiUrl = getProperty("api_url")
}

class FeatureB : KoinComponent {
    val apiUrl = getProperty("api_url")
}

// ✅ Better - Centralize configuration
class AppConfig(
    val apiUrl: String,
    val apiKey: String,
    val timeout: Int
)

module {
    single {
        AppConfig(
            apiUrl = getProperty("api_url"),
            apiKey = getProperty("api_key"),
            timeout = getProperty("timeout", "30").toInt()
        )
    }

    single { ApiClient(get<AppConfig>().apiUrl) }
}
```

## Testing

When testing components, you can override Koin configuration:

```kotlin
class MyComponent : KoinComponent {
    val service: MyService by inject()

    fun doWork() = service.execute()
}

@Test
fun testComponent() {
    // Setup test Koin instance
    startKoin {
        modules(module {
            single<MyService> { MockMyService() }
        })
    }

    val component = MyComponent()
    val result = component.doWork()

    assertEquals("mock result", result)

    // Cleanup
    stopKoin()
}
```

## See Also

- [Start Koin](/docs/reference/koin-core/start-koin) - Initializing Koin
- [Definitions](/docs/reference/koin-core/definitions) - Creating definitions
- [Injection Parameters](/docs/reference/koin-core/injection-parameters) - Runtime parameters
- [Android Injection](/docs/reference/koin-android/get-instances) - Android-specific injection