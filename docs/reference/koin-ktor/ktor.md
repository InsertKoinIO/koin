---
title: Koin for Ktor
---

The `koin-ktor` module provides dependency injection integration for Ktor applications, working alongside Ktor's built-in DI system.

## Why Koin for Ktor?

Ktor 3.4+ includes a built-in DI system. Here's how they compare:

| Feature | Ktor DI | Koin |
|---------|---------|------|
| Basic injection | Yes | Yes |
| Qualifiers (`@Named`) | Yes | Yes |
| Property injection | Yes (`@Property`) | Yes |
| Nullable/optional dependencies | Yes | Yes |
| Scopes (request, custom) | No | Yes |
| Module organization | No | Yes |
| Lazy modules | No | Yes |
| Annotation-based components | No | Yes |
| Compiler plugin verification | No | Yes |

### Ktor DI Limitations

- **No scoping** - No request or custom scopes, only singleton-like behavior with cleanup ordering
- **No annotation-based components** - No `@Singleton`, `@Factory` component scanning like Koin Annotations
- **No compile-time verification** - No compiler plugin to verify DI configuration before runtime
- **Limited parameterized types** - Cannot resolve parameterized types across type argument subtypes

**When to use Koin:**
- Scoped dependencies (request scope, custom scopes)
- Module-based organization
- Annotation-based component scanning
- Compile-time verification with compiler plugin

**When Ktor DI is sufficient:**
- Simple applications with few dependencies
- No scoping requirements
- Basic qualifier needs

## Setup

Add the Koin Ktor dependency:

```kotlin
dependencies {
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version") // Optional
}
```

## Declaring Dependencies

Koin supports multiple DSL approaches.

### Compiler Plugin DSL

The simplest syntax:

```kotlin
val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

### Annotations

Spring-like with compile-time verification:

```kotlin
@Module
@ComponentScan("com.example")
class AppModule

@Singleton
class UserRepositoryImpl : UserRepository

@Singleton
class UserService(private val repository: UserRepository)
```

### Classic DSL

Constructor references:

```kotlin
val appModule = module {
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::UserService)
}
```

## Installing the Koin Plugin

Install Koin in your `Application` module:

```kotlin
fun Application.main() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
```

### Complete Configuration

```kotlin
fun Application.main() {
    install(Koin) {
        slf4jLogger()
        fileProperties("/application.conf")
        modules(
            networkModule,
            repositoryModule,
            serviceModule
        )
        createEagerInstances()
    }
}
```

## Dependency Injection

Koin provides extension functions for Ktor's core types.

### Injection Points

`inject()` and `get()` work in:
- `Application`
- `Route`
- `Routing`
- `ApplicationCall` (within route handlers)

### Application-Level

```kotlin
fun Application.main() {
    val helloService by inject<HelloService>()  // Lazy
    val configService = get<ConfigService>()     // Eager

    routing {
        get("/hello") {
            call.respondText(helloService.sayHello())
        }
    }
}
```

### Route-Level

```kotlin
fun Route.customerRoutes() {
    val customerService by inject<CustomerService>()

    get("/customers") {
        call.respond(customerService.getAllCustomers())
    }

    get("/customers/{id}") {
        val id = call.parameters["id"]?.toInt()
            ?: return@get call.respond(HttpStatusCode.BadRequest)
        call.respond(customerService.getCustomer(id))
    }
}
```

### Request Handler

```kotlin
routing {
    get("/users/{id}") {
        val userService = get<UserService>()
        val userId = call.parameters["id"]!!
        call.respond(userService.getUser(userId))
    }
}
```

## Ktor Events

Monitor Koin lifecycle events:

| Event | Description |
|-------|-------------|
| `KoinApplicationStarted` | Koin container started |
| `KoinApplicationStopPreparing` | Koin preparing to stop |
| `KoinApplicationStopped` | Koin container stopped |

```kotlin
fun Application.main() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin started")
        get<CacheWarmer>().warmUp()
    }

    environment.monitor.subscribe(KoinApplicationStopped) {
        log.info("Koin stopped")
    }
}
```

## Quick Reference

| Function | Description |
|----------|-------------|
| `install(Koin) { }` | Install Koin plugin |
| `inject<T>()` | Lazy injection |
| `get<T>()` | Eager injection |
| `koinModule { }` | Declare inline module |
| `koinModules(...)` | Load existing modules |

## Documentation

| Topic | Description |
|-------|-------------|
| **[DI Bridge](/docs/reference/koin-ktor/ktor-bridge)** | Koin ↔ Ktor DI integration |
| **[Request Scopes](/docs/reference/koin-ktor/ktor-scopes)** | Request-scoped dependencies |
| **[Testing](/docs/reference/koin-ktor/ktor-testing)** | Testing Ktor with Koin |
| **[Isolated Context](/docs/reference/koin-ktor/ktor-isolated)** | Isolated Koin instances |

## Related

- **[Tutorial: Ktor](/docs/quickstart/ktor)** - Step-by-step tutorial
- **[Tutorial: Ktor with Annotations](/docs/quickstart/ktor-annotations)** - Annotations tutorial
- **[Koin Annotations](/docs/reference/koin-annotations/start)** - Annotation reference
- **[Ktor Documentation](https://ktor.io/)** - Official Ktor docs
