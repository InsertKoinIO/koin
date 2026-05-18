---
title: What is Dependency Injection?
---

# What is Dependency Injection?

Dependency Injection (DI) is a design pattern where objects receive their dependencies from external sources rather than creating them internally. This promotes loose coupling, better testability, and cleaner code architecture.

## What is a Dependency?

A dependency is any object that another object needs to function. For example, a `Car` depends on an `Engine` to drive.

### Without Dependency Injection

```kotlin
class Engine {
    fun start() {
        println("Engine starting...")
    }
}

class Car {
    private val engine = Engine()  // Car creates its own engine

    fun drive() {
        engine.start()
        println("Car is driving")
    }
}
```

**Problems with this approach:**
- `Car` is tightly coupled to a specific `Engine` implementation
- Difficult to test `Car` independently
- Hard to swap engine types (electric, diesel, etc.)
- `Car` controls the lifecycle of `Engine`

### With Dependency Injection

```kotlin
class Car(private val engine: Engine) {  // Engine is injected
    fun drive() {
        engine.start()
        println("Car is driving")
    }
}

// Now we can easily provide different engines
val gasolineCar = Car(GasEngine())
val electricCar = Car(ElectricEngine())
```

**Benefits:**
- `Car` doesn't know how `Engine` is created
- Easy to test with mock engines
- Flexible - can swap implementations
- Clear dependencies visible in constructor

## Three Ways to Provide Dependencies

### 1. Constructor Injection (Recommended)

Dependencies are passed through the constructor:

```kotlin
class UserRepository(
    private val database: Database,
    private val apiClient: ApiClient
) {
    fun getUser(id: String): User {
        return database.query(id) ?: apiClient.fetchUser(id)
    }
}
```

**Advantages:**
- Dependencies are explicit and required
- Immutable (using `val`)
- Easy to test
- Clear dependency graph

**With Koin:**

```kotlin
val appModule = module {
    single<Database>()
    single<ApiClient>()
    single<UserRepository>()  // Koin auto-wires dependencies
}
```

:::info
Constructor injection is the **preferred approach** in Koin. It makes your code testable without requiring Koin in unit tests.
:::

### 2. Field Injection

Dependencies are injected into class properties:

```kotlin
class UserActivity : AppCompatActivity() {
    // Lazy injection - instance created when first accessed
    private val viewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadUser()  // ViewModel instance created here
    }
}
```

**When to use:**
- Android framework classes (Activity, Fragment, Service) where you don't control construction
- When constructor injection isn't possible

**With Koin:**

```kotlin
// Lazy injection
val presenter: Presenter by inject()

// Eager injection
val presenter: Presenter = get()
```

### 3. Method Injection

Dependencies are passed through methods (less common):

```kotlin
class ReportGenerator {
    fun generateReport(data: DataSource) {
        // Use data to generate report
    }
}
```

**When to use:**
- Optional dependencies
- Dependencies that change during object lifetime
- Callback patterns

## Manual vs Automated Dependency Injection

### The Problem with Manual DI

As applications grow, managing dependencies manually becomes complex:

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manually creating the entire dependency graph
        val database = Database()
        val apiClient = ApiClient()
        val userRepository = UserRepository(database, apiClient)
        val authRepository = AuthRepository(database, apiClient)
        val userService = UserService(userRepository, authRepository)
        val viewModel = UserViewModel(userService)

        // Finally can use viewModel...
    }
}
```

**Problems:**
- Repetitive code across Activities/Fragments
- Easy to make mistakes in dependency order
- Hard to maintain as the app grows
- Difficult to manage lifecycles (singletons, scoped objects)
- No centralized configuration

### The Container Pattern (Manual Approach)

Developers often create a container to centralize object creation:

```kotlin
object AppContainer {
    private val database by lazy { Database() }
    private val apiClient by lazy { ApiClient() }

    val userRepository by lazy { UserRepository(database, apiClient) }
    val authRepository by lazy { AuthRepository(database, apiClient) }

    fun createUserViewModel() = UserViewModel(
        UserService(userRepository, authRepository)
    )
}

// Usage
class MainActivity : AppCompatActivity() {
    private val viewModel = AppContainer.createUserViewModel()
}
```

**Still has issues:**
- Manual wiring of dependencies
- No automatic lifecycle management
- Global state (singleton container)
- Still repetitive for complex graphs

### How Koin Solves This

Koin provides automated dependency resolution with your choice of **DSL or Annotations**:

```kotlin
// Define dependencies once
val appModule = module {
    single<Database>()
    single<ApiClient>()
    single<UserRepository>()
    single<AuthRepository>()
    single<UserService>()
    viewModel<UserViewModel>()
}

// Start Koin once
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}

// Use anywhere - Koin handles the entire dependency graph
class MainActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModel()
    // That's it! Koin creates UserViewModel and all its dependencies
}
```

**Koin advantages:**
- Declarative dependency configuration
- Automatic dependency resolution
- Lifecycle management (singleton, factory, scoped)
- Type-safe injection
- Easy testing and module replacement

## Automated DI Solutions

There are different approaches to automated dependency injection:

| Approach | Examples | How it works |
|----------|----------|--------------|
| **Reflection-based** | (older frameworks) | Uses reflection at runtime |
| **Code generation** | Dagger, Hilt | Generates code at compile time (annotation processing) |
| **Compiler plugins** | Koin Compiler Plugin | Native compiler integration for DSL & Annotations |
| **DSL-based** | Koin (classic) | Runtime DSL configuration |

**Koin's approach - DSL & Annotations, both equally powerful:**
- **DSL style:** Clean Kotlin DSL configuration (`single<MyService>()`, `viewModel<MyVM>()`)
- **Annotation style:** Familiar annotations (`@Singleton`, `@KoinViewModel`)
- Both powered by the same Compiler Plugin for compile-time safety
- No reflection, lightweight
- Choose the style that fits your team

## Service Locator vs Dependency Injection

It's important to understand the difference:

### Service Locator Pattern

Components actively request dependencies from a registry:

```kotlin
class UserService : KoinComponent {
    private val repository: UserRepository by inject()  // "Pulling" dependency
}
```

### Dependency Injection Pattern

Dependencies are provided from outside:

```kotlin
class UserService(
    private val repository: UserRepository  // "Pushed" into component
)
```

### Comparison

| Aspect | Service Locator | Dependency Injection |
|--------|----------------|---------------------|
| Dependency visibility | Hidden inside class | Explicit in constructor |
| Testing | Requires framework | Easy - pass test doubles |
| Coupling | Depends on container | Depends on interfaces |
| Usage in Koin | `get()`, `by inject()` | Constructor with Koin module |
| Best for | Android framework classes | Business logic, services |

### Best Practices with Koin

1. **Prefer Constructor Injection** for business logic:

```kotlin
// Good - testable without Koin
class UserViewModel(private val userService: UserService) : ViewModel()

val appModule = module {
    viewModel<UserViewModel>()  // Koin resolves dependencies
}
```

2. **Use Service Locator** only when necessary:

```kotlin
// Acceptable - Activity construction controlled by Android
class UserActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModel()
}
```

3. **Avoid `KoinComponent` in business logic:**

```kotlin
// Bad - hard to test
class UserService : KoinComponent {
    private val repository: UserRepository = get()
}

// Good - explicit dependencies
class UserService(private val repository: UserRepository)
```

## Benefits of Dependency Injection

### 1. Testability

Without DI, testing is difficult:

```kotlin
class UserService {
    private val repository = UserRepository()  // Can't mock!
}
```

With DI, testing is straightforward:

```kotlin
class UserService(private val repository: UserRepository)

@Test
fun testGetUser() {
    val mockRepository = mockk<UserRepository>()
    val service = UserService(mockRepository)  // Full control

    every { mockRepository.findUser("123") } returns testUser
    assertEquals(testUser, service.getUser("123"))
}
```

### 2. Flexibility

Easily swap implementations:

```kotlin
val appModule = module {
    single<EmailService> { GmailService() }  // Production
}

val testModule = module {
    single<EmailService> { MockEmailService() }  // Testing
}
```

### 3. Code Organization

Centralized dependency configuration:

```kotlin
val dataModule = module {
    single<Database>()
    single<ApiClient>()
}

val domainModule = module {
    single<UserRepository>()
    single<AuthRepository>()
}

val presentationModule = module {
    viewModel<UserViewModel>()
}

startKoin {
    modules(dataModule, domainModule, presentationModule)
}
```

### 4. Lifecycle Management

Koin handles object lifecycles:

```kotlin
val appModule = module {
    single<Database>()       // One instance for entire app
    factory<Presenter>()     // New instance each time
    scoped<SessionData>()    // Instance per scope
}
```

## Summary

Dependency Injection is a powerful pattern that:
- **Decouples** components from their dependencies
- **Improves testability** by allowing dependency replacement
- **Simplifies maintenance** with centralized configuration
- **Scales better** than manual dependency management

Koin makes DI in Kotlin simple by:
- Offering **two equally powerful styles**: DSL or Annotations - your choice
- Supporting both **constructor injection** (recommended) and **field injection** (when needed)
- Providing **compile-time safety** with the Compiler Plugin
- Requiring **zero reflection** - pure Kotlin

## Next Steps

- **[What is Koin?](/docs/intro/what-is-koin)** - Learn about Koin's approach
- **[Koin Compiler Plugin](/docs/intro/koin-compiler-plugin)** - The recommended, safer approach
- **[Setup Guide](/docs/setup/gradle)** - Add Koin to your project
