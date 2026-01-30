---
title: Request Scopes
---

# Request Scopes in Ktor

Request scopes create instances that live for the duration of a single HTTP request, perfect for request-specific data and processing.

## Declaring Request-Scoped Components

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

## Accessing Request-Scoped Components

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

## Injecting ApplicationCall

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

## Scope Lifecycle Callbacks

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

## Declaring Modules in Ktor

Koin provides convenient functions to declare modules directly within your Ktor application.

### Using koinModule

Declare modules inline:

```kotlin
fun Application.configureRouting() {
    koinModule {
        singleOf(::CustomerRepository)
        singleOf(::CustomerService)
    }

    routing {
        customerRoutes()
    }
}
```

### Using koinModules

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

## Modular Application Structure

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

    customerModule()
    orderModule()
}
```

## Request Scope with Annotations

Use annotations for request-scoped components:

```kotlin
@Scope(RequestScope::class)
class RequestLogger(private val call: ApplicationCall) {
    fun log(message: String) {
        println("[${call.request.path()}] $message")
    }
}
```

## Complete Example

```kotlin
val appModule = module {
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::UserService)

    requestScope {
        scopedOf(::RequestLogger)
        scopedOf(::RequestMetrics)

        onCreate { metrics: RequestMetrics ->
            metrics.start()
        }

        onClose { metrics: RequestMetrics ->
            metrics.recordDuration()
        }
    }
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    routing {
        get("/api/users/{id}") {
            val logger = call.scope.get<RequestLogger>()
            val userService = get<UserService>()

            logger.log("Fetching user")
            val user = userService.getUser(call.parameters["id"]!!)

            call.respond(user)
        }
    }
}
```

## API Reference

| Function | Description |
|----------|-------------|
| `requestScope { }` | Declare request-scoped components |
| `call.scope.get<T>()` | Get request-scoped instance |
| `onCreate { }` | Callback when scope is created |
| `onClose { }` | Callback when scope is closed |
| `koinModule { }` | Declare inline module |
| `koinModules(...)` | Load existing modules |

## See Also

- **[Koin for Ktor](/docs/reference/koin-ktor/ktor)** - Main Ktor documentation
- **[Scopes](/docs/reference/koin-core/scopes)** - Core scope concepts
