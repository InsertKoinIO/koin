---
title: What is Dependency Injection?
description: Learn what dependency injection is, the ways to provide dependencies, manual versus automated DI, and how it differs from a service locator.
---

Dependency injection (DI) is a design pattern where an object receives the objects it needs from outside instead of creating them itself. This page explains the pattern without assuming any framework, then shows where a DI container such as Koin fits. If you already know DI, go to [Core Concepts](/docs/intro/core-concepts).

## What is a dependency?

A dependency is an object that another object needs to do its work. A `Car` needs an `Engine` to drive.

### Without dependency injection

```kotlin
class Engine {
    fun start() {
        println("Engine starting...")
    }
}

class Car {
    private val engine = Engine() // Car creates its own engine

    fun drive() {
        engine.start()
        println("Car is driving")
    }
}
```

This works, but it has costs:

- **Tight coupling**: `Car` is tied to one `Engine` implementation.
- **Hard to test**: you can't test `Car` with a fake engine.
- **Hard to change**: switching to an electric engine means editing `Car`.
- **Lifecycle in the wrong place**: `Car` decides when an `Engine` is created and how many exist.

### With dependency injection

```kotlin
class Car(private val engine: Engine) {
    fun drive() {
        engine.start()
        println("Car is driving")
    }
}

val gasolineCar = Car(GasEngine())
val electricCar = Car(ElectricEngine())
```

`Car` no longer knows how an `Engine` is built. You can pass any implementation, including a fake in tests, and the constructor shows exactly what `Car` needs.

## Ways to provide dependencies

### Constructor injection

Dependencies are constructor parameters:

```kotlin
class UserRepository(
    private val database: Database,
    private val apiClient: ApiClient,
) {
    fun getUser(id: String): User =
        database.query(id) ?: apiClient.fetchUser(id)
}
```

Dependencies are explicit, required, and can be immutable (`val`). The class can be created and tested without any framework. This is the approach to use by default.

### Field injection

Dependencies are assigned to properties after the object is created. This is needed when a framework creates the object and you don't control its constructor, as with an Android `Activity`:

```kotlin
class UserActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModel() // filled by Koin on first access
}
```

Use it only at these framework entry points. Below them, use constructor injection.

### Method injection

A dependency is passed to the method that needs it:

```kotlin
class ReportGenerator {
    fun generateReport(data: DataSource) {
        ...
    }
}
```

This fits a dependency that changes on every call, or one that only one method uses.

## Manual and automated dependency injection

### Wiring by hand

You can apply DI without any library, by building the object graph yourself:

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = Database()
        val apiClient = ApiClient()
        val userRepository = UserRepository(database, apiClient)
        val authRepository = AuthRepository(database, apiClient)
        val userService = UserService(userRepository, authRepository)
        val viewModel = UserViewModel(userService)
    }
}
```

As the app grows, this code is repeated in many places, must be updated whenever a constructor changes, and gives no control over how long each object lives.

### A container written by hand

A common next step is a container object that centralizes creation:

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

class MainActivity : AppCompatActivity() {
    private val viewModel = AppContainer.createUserViewModel()
}
```

This removes the repetition, but you still write and maintain every constructor call, and lifecycles beyond "one instance" or "a new instance" (an instance per screen, per session) need more hand-written code.

### A DI container

A DI library automates the container. You declare which classes exist and how long they live, and the library builds the graph. With Koin:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
val appModule = module {
    single<Database>()
    single<ApiClient>()
    single<UserRepository>()
    single<AuthRepository>()
    single<UserService>()
    viewModel<UserViewModel>()
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
@Singleton
class UserRepository(private val database: Database, private val apiClient: ApiClient)

@Singleton
class UserService(private val userRepository: UserRepository, private val authRepository: AuthRepository)

@KoinViewModel
class UserViewModel(private val userService: UserService) : ViewModel()

// Database, ApiClient and AuthRepository are annotated the same way

@Module
@ComponentScan("com.example.app")
class AppModule
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
val appModule = module {
    singleOf(::Database)
    singleOf(::ApiClient)
    singleOf(::UserRepository)
    singleOf(::AuthRepository)
    singleOf(::UserService)
    viewModelOf(::UserViewModel)
}
```

</TabItem>
</Tabs>

The Activity then asks for the top of the graph:

```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModel()
}
```

The container creates `UserViewModel` and everything below it, keeps one `Database` for the whole app, and gives a new `UserViewModel` to each screen. [Core Concepts](/docs/intro/core-concepts) explains these pieces.

## How DI libraries build the graph

DI libraries differ in when and how they build the graph:

| Approach | How it works | Examples |
|----------|--------------|----------|
| Reflection | Reads constructors at runtime and calls them by reflection | Guice, Spring |
| Annotation processing | Generates factory source code at build time | Dagger, Hilt |
| Compiler plugin | Rewrites code inside the compiler and checks the graph at build time | Koin Compiler Plugin |
| Runtime DSL | You write the factories as Kotlin lambdas, the container runs them at runtime | Koin Classic DSL |

Koin keeps a runtime container in all cases. The Compiler Plugin adds the wiring and the build-time checks on top. See [What is Koin?](/docs/intro/what-is-koin) for what this means in practice.

## Service locator and dependency injection

The two patterns are often confused.

With a **service locator**, a class asks a registry for its dependencies:

```kotlin
class UserService : KoinComponent {
    private val repository: UserRepository by inject() // pulled from the container
}
```

With **dependency injection**, the dependencies are given to the class:

```kotlin
class UserService(
    private val repository: UserRepository // pushed in from outside
)
```

| Aspect | Service locator | Dependency injection |
|--------|-----------------|----------------------|
| Visibility of dependencies | Hidden inside the class | Visible in the constructor |
| Testing | Needs the container, or a test version of it | Pass fakes to the constructor |
| Coupling | The class depends on the container | The class depends only on its parameters |
| In Koin | `get()`, `by inject()`, `KoinComponent` | Constructor parameters resolved by a module |
| Good fit | Framework classes you don't construct | Business logic, repositories, services |

Koin supports both. Use dependency injection for your own classes, and the service locator style only where a framework creates the object (an `Activity`, a `Fragment`, a Ktor route):

```kotlin
// avoid: hidden dependency, needs Koin in tests
class UserService : KoinComponent {
    private val repository: UserRepository = get()
}

// prefer: explicit dependency
class UserService(private val repository: UserRepository)
```

## What you gain

### Testability

```kotlin
class UserService(private val repository: UserRepository)

@Test
fun getUser() {
    val repository = mockk<UserRepository>()
    every { repository.findUser("123") } returns testUser

    val service = UserService(repository)

    assertEquals(testUser, service.getUser("123"))
}
```

A class that receives its dependencies can be tested by calling its constructor. No container is needed.

### Swappable implementations

```kotlin
val appModule = module {
    single<EmailService> { SmtpEmailService() }
}

val testModule = module {
    single<EmailService> { FakeEmailService() }
}
```

Consumers depend on `EmailService`. Which implementation they get is decided in one place, the module.

### Central configuration

```kotlin
startKoin {
    modules(dataModule, domainModule, presentationModule)
}
```

The whole object graph is described by a few modules, usually one per layer or feature, instead of being spread across the code.

### Lifecycle management

```kotlin
val appModule = module {
    single<Database>()     // one instance for the app
    factory<Presenter>()   // a new instance on every request

    scope<UserSession> {
        scoped<CartRepository>() // one instance per session
    }
}
```

The container decides how long each object lives, so classes don't manage the lifetime of their dependencies. See [Scopes](/docs/reference/koin-core/scopes).

## Related

- [What is Koin?](/docs/intro/what-is-koin): how Koin implements a DI container.
- [Core Concepts](/docs/intro/core-concepts): definitions, modules, injection and scopes in Koin.
- [Retrieving Dependencies](/docs/reference/koin-core/injection): `get()` and `inject()` in detail.
- [Injecting in Tests](/docs/reference/koin-test/testing): test code that uses Koin.
