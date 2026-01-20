---
title: Dependency Injection in Ktor
---

The `koin-ktor` module provides seamless dependency injection integration for Ktor applications. It offers a dedicated Koin plugin for Ktor that works alongside Ktor's built-in DI system.

## Setup

Add the Koin Ktor dependency to your project:

```kotlin
dependencies {
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version") // Optional logging
}
```

## Installing the Koin Plugin

Install Koin as a Ktor plugin in your `Application` module:

```kotlin
fun Application.main() {
    // Install Koin plugin
    install(Koin) {
        // SLF4J Koin logger
        slf4jLogger()

        // Declare modules
        modules(appModule)
    }
}
```

### Complete Configuration

```kotlin
fun Application.main() {
    install(Koin) {
        // Logging
        slf4jLogger()

        // Properties
        fileProperties("/application.conf")

        // Modules
        modules(
            networkModule,
            repositoryModule,
            serviceModule
        )

        // Create eager instances
        createEagerInstances()
    }
}
```

## Ktor DI Bridge (Koin 4.2+ / Ktor 3.4+)

:::info Experimental Feature
The Ktor DI Bridge is an experimental feature that enables bidirectional dependency resolution between Koin and Ktor DI. Use the `bridge { }` DSL to configure the integration.
:::

Koin 4.2+ provides seamless integration with **Ktor 3.4+** built-in dependency injection system through a configurable bridge.

### Bridge Configuration

Use the `bridge { }` DSL to enable bidirectional dependency resolution:

```kotlin
fun Application.module() {
    install(Koin) {
        slf4jLogger()

        // Configure the DI bridge
        bridge {
            ktorToKoin()  // Ktor DI can resolve Koin dependencies
            koinToKtor()  // Koin can resolve Ktor DI dependencies
        }

        modules(appModule)
    }

    // Ktor DI dependencies
    dependencies {
        provide<KtorSpecificService> { KtorSpecificServiceImpl() }
    }
}
```

### Bridge Options

| Option | Description |
|--------|-------------|
| `ktorToKoin()` | Allows Ktor's `by dependencies` delegate to resolve dependencies from Koin modules |
| `koinToKtor()` | Allows Koin's `inject()` and `get()` to resolve dependencies from Ktor DI |

### Using `ktorToKoin()` - Resolve Koin from Ktor DI

When `ktorToKoin()` is enabled, you can use Ktor's `by dependencies` delegate to resolve Koin dependencies:

```kotlin
fun Application.module() {
    install(Koin) {
        bridge {
            ktorToKoin()  // Enable Ktor -> Koin resolution
        }
        modules(module {
            single<HelloService> { HelloServiceImpl() }
        })
    }

    routing {
        get("/hello") {
            // Resolve Koin dependency using Ktor's delegate
            val helloService: HelloService by dependencies
            call.respondText(helloService.sayHello())
        }
    }
}
```

### Using `koinToKtor()` - Resolve Ktor DI from Koin

When `koinToKtor()` is enabled, Koin's `inject()` can resolve dependencies declared in Ktor DI:

```kotlin
fun Application.module() {
    install(Koin) {
        bridge {
            koinToKtor()  // Enable Koin -> Ktor resolution
        }
        modules(appModule)
    }

    // Declare dependency in Ktor DI
    dependencies {
        provide<DatabaseConnection> { DatabaseConnectionImpl() }
    }

    routing {
        get("/data") {
            // Resolve Ktor DI dependency using Koin's inject
            val database: DatabaseConnection by inject()
            call.respondText(database.query())
        }
    }
}
```

### Full Bidirectional Example

Enable both directions for maximum flexibility:

```kotlin
fun Application.module() {
    // Install Koin with full bridge
    install(Koin) {
        slf4jLogger()

        bridge {
            ktorToKoin()  // Ktor can use Koin deps
            koinToKtor()  // Koin can use Ktor deps
        }

        modules(module {
            single<HelloService> { HelloServiceImpl() }
        })
    }

    // Ktor DI dependencies
    dependencies {
        provide<KtorSpecificService> { KtorSpecificServiceImpl() }
    }

    routing {
        // Using Koin's inject() for both
        get("/mixed-koin") {
            val helloService: HelloService by inject()        // From Koin
            val ktorService: KtorSpecificService by inject()  // From Ktor DI via bridge
            call.respondText("${helloService.sayHello()} - ${ktorService.process()}")
        }

        // Using Ktor's dependencies delegate for both
        get("/mixed-ktor") {
            val helloService: HelloService by dependencies        // From Koin via bridge
            val ktorService: KtorSpecificService by dependencies  // From Ktor DI
            call.respondText("${helloService.sayHello()} - ${ktorService.process()}")
        }
    }
}
```

### Architecture Benefits

This integration enables a clean separation of concerns:
- **Ktor DI** - Framework-level dependencies (Database connections, Configuration, Infrastructure)
- **Koin** - Application-level dependencies (Repositories, Services, Use Cases)

```kotlin
fun Application.module() {
    // Ktor DI - Infrastructure layer
    val config = environment.config
    val database = Database(config)
    dependencies {
        provide<Database> { database }
        provide<ApplicationConfig> { config }
    }

    // Koin - Application layer with bridge
    install(Koin) {
        slf4jLogger()

        bridge {
            koinToKtor()  // Allow Koin to resolve infrastructure deps
        }

        modules(appModule)
    }
}

val appModule = module {
    // Koin definitions can use Ktor DI dependencies via bridge
    singleOf(::CustomerRepository)    // Injects Database from Ktor DI
    singleOf(::OrderRepository)       // Injects Database from Ktor DI
    singleOf(::CustomerService)       // Injects CustomerRepository from Koin
}
```


## Dependency Injection in Ktor

Koin provides extension functions for Ktor's core types, making dependency injection available throughout your application.

### Available Injection Points

Koin `inject()` and `get()` functions work in:
- `Application`
- `Route`
- `Routing`
- `ApplicationCall` (within route handlers)

### Application-Level Injection

Inject dependencies at the application level:

```kotlin
fun Application.main() {
    // Lazy injection
    val helloService by inject<HelloService>()

    // Or eager injection
    val configService = get<ConfigService>()

    routing {
        get("/hello") {
            call.respondText(helloService.sayHello())
        }

        get("/config") {
            call.respondText(configService.getConfig())
        }
    }
}
```

### Route-Level Injection

Inject per route or routing block:

```kotlin
fun Route.customerRoutes() {
    val customerService by inject<CustomerService>()

    get("/customers") {
        val customers = customerService.getAllCustomers()
        call.respond(customers)
    }

    get("/customers/{id}") {
        val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val customer = customerService.getCustomer(id)
        call.respond(customer)
    }
}
```

### Request Handler Injection

Inject directly in route handlers:

```kotlin
routing {
    get("/users/{id}") {
        val userService = get<UserService>()
        val userId = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)

        val user = userService.getUser(userId)
        call.respond(user)
    }
}
```

## Request Scopes (4.1+)

Request scopes create instances that live for the duration of a single HTTP request, perfect for request-specific data and processing.

### Declaring Request-Scoped Components

Use `requestScope` to declare components bound to the request lifecycle:

```kotlin
val appModule = module {
    // Singleton - shared across all requests
    single { UserRepository() }

    // Request scope - new instance per request
    requestScope {
        scopedOf(::RequestLogger)
        scopedOf(::RequestMetrics)
        scopedOf(::UserSessionHandler)
    }
}
```

### Accessing Request-Scoped Components

Use `call.scope.get()` to resolve request-scoped dependencies:

```kotlin
routing {
    get("/users/{id}") {
        val requestLogger = call.scope.get<RequestLogger>()
        val metrics = call.scope.get<RequestMetrics>()

        metrics.start()
        requestLogger.log("Processing user request")

        val userId = call.parameters["id"]!!
        val userService = get<UserService>()
        val user = userService.getUser(userId)

        requestLogger.log("Request completed")
        metrics.end()

        call.respond(user)
    }
}
```

### Injecting ApplicationCall

Request-scoped components can automatically inject `ApplicationCall`:

```kotlin
class RequestLogger(private val call: ApplicationCall) {
    fun log(message: String) {
        val requestPath = call.request.path()
        val method = call.request.httpMethod.value
        println("[$method $requestPath] $message")
    }
}

class UserSessionHandler(private val call: ApplicationCall) {
    fun getUserId(): String? {
        return call.request.headers["X-User-ID"]
    }

    fun isAuthenticated(): Boolean {
        return call.request.headers["Authorization"] != null
    }
}
```

### Request Scope Lifecycle

```kotlin
requestScope {
    scoped { RequestContext(get()) }

    // onCreate callback
    onCreate { requestContext ->
        requestContext.startTime = System.currentTimeMillis()
    }

    // onClose callback
    onClose { requestContext ->
        val duration = System.currentTimeMillis() - requestContext.startTime
        println("Request completed in ${duration}ms")
    }
}
```

:::note
Request scopes are **created and destroyed for each HTTP request**. Instances are not shared between requests, ensuring thread safety and preventing state leakage.
:::

## Declaring Modules in Ktor (4.1+)

Koin provides convenient functions to declare modules directly within your Ktor application setup.

### Using `koinModule`

Declare modules inline using `Application.koinModule`:

```kotlin
fun Application.configureRouting() {
    // Declare Koin module specific to this feature
    koinModule {
        singleOf(::CustomerRepository)
        singleOf(::CustomerService)
    }

    routing {
        customerRoutes()
    }
}
```

### Using `koinModules`

Load multiple existing modules:

```kotlin
fun Application.configureCustomerFeature() {
    koinModules(
        customerRepositoryModule,
        customerServiceModule,
        customerRoutesModule
    )

    routing {
        customerRoutes()
    }
}
```

### Modular Application Structure

Organize your Ktor app by feature:

```kotlin
// Feature 1: Customer Management
fun Application.customerModule() {
    koinModule {
        singleOf(::CustomerRepository)
        singleOf(::CustomerService)
    }

    routing {
        route("/api/customers") {
            customerRoutes()
        }
    }
}

// Feature 2: Order Management
fun Application.orderModule() {
    koinModule {
        singleOf(::OrderRepository)
        singleOf(::OrderService)
    }

    routing {
        route("/api/orders") {
            orderRoutes()
        }
    }
}

// Main application
fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(coreModule)
    }

    // Install feature modules
    customerModule()
    orderModule()
}
```

## Ktor Events

Monitor Koin lifecycle events within your Ktor application:

### Available Events

| Event | Description |
|-------|-------------|
| `KoinApplicationStarted` | Koin container started successfully |
| `KoinApplicationStopPreparing` | Koin container preparing to stop |
| `KoinApplicationStopped` | Koin container stopped |

### Subscribing to Events

```kotlin
fun Application.main() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    // Listen to Koin lifecycle events
    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin started successfully")
        // Perform post-startup tasks
        get<CacheWarmer>().warmUp()
    }

    environment.monitor.subscribe(KoinApplicationStopPreparing) {
        log.info("Koin stopping - preparing shutdown")
        // Prepare for shutdown
        get<ConnectionPool>().drain()
    }

    environment.monitor.subscribe(KoinApplicationStopped) {
        log.info("Koin stopped")
        // Cleanup complete
    }
}
```

### Use Cases for Events

- **Startup**: Warm caches, initialize background jobs, verify connections
- **Shutdown**: Close connections, flush buffers, save state
- **Monitoring**: Track application lifecycle, log metrics

## Complete Example

A full Ktor + Koin application:

```kotlin
// Domain
data class User(val id: Int, val name: String, val email: String)

interface UserRepository {
    suspend fun findAll(): List<User>
    suspend fun findById(id: Int): User?
}

class UserRepositoryImpl(private val database: Database) : UserRepository {
    override suspend fun findAll(): List<User> {
        return database.query("SELECT * FROM users")
    }

    override suspend fun findById(id: Int): User? {
        return database.queryOne("SELECT * FROM users WHERE id = ?", id)
    }
}

class UserService(private val repository: UserRepository) {
    suspend fun getAllUsers() = repository.findAll()
    suspend fun getUser(id: Int) = repository.findById(id)
}

// Koin Module
val appModule = module {
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::UserService)

    requestScope {
        scopedOf(::RequestLogger)
    }
}

// Ktor Application
fun Application.module() {
    // Setup Database (Ktor DI)
    val database = Database(environment.config)
    dependencies {
        provide<Database> { database }
    }

    // Install Koin with bridge
    install(Koin) {
        slf4jLogger()

        // Enable Koin to resolve Database from Ktor DI
        bridge {
            koinToKtor()
        }

        modules(appModule)
    }

    // Configure routing
    routing {
        userRoutes()
    }
}

fun Route.userRoutes() {
    val userService by inject<UserService>()

    get("/api/users") {
        val logger = call.scope.get<RequestLogger>()
        logger.log("Fetching all users")

        val users = userService.getAllUsers()
        call.respond(users)
    }

    get("/api/users/{id}") {
        val logger = call.scope.get<RequestLogger>()
        val id = call.parameters["id"]?.toInt()
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        logger.log("Fetching user $id")

        val user = userService.getUser(id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(user)
    }
}
```

## Best Practices

### Module Organization

1. **Separate infrastructure from application** - Use Ktor DI for infrastructure, Koin for business logic
2. **Feature-based modules** - Group related services in feature modules
3. **Request scopes for request-specific data** - Use request scopes for request context, logging, metrics

### Performance

1. **Use singletons for stateless services** - Repositories, services, utilities
2. **Request scopes for request data** - Avoid storing request state in singletons
3. **Lazy injection when possible** - Use `by inject()` for deferred initialization

### Testing

1. **Use Koin test modules** - Override production modules with test implementations
2. **Clean up between tests** - Stop Koin after each test to reset state
3. **Test routes independently** - Mock services and test routing logic separately

## See Also

- [Koin Core](/docs/reference/koin-core/start-koin) - Core Koin concepts
- [Modules](/docs/reference/koin-core/modules) - Module organization
- [Scopes](/docs/reference/koin-core/scopes) - Scoped dependencies
- [Ktor Documentation](https://ktor.io/) - Official Ktor documentation

