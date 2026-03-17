---
title: Isolated Context
---

# Isolated Context in Ktor

The `KoinIsolated` plugin runs Koin in an isolated context, separate from the global Koin instance. This is useful for testing, multi-tenant applications, and running multiple Koin instances.

## When to Use Isolated Context

- **Testing** - Each test gets its own isolated Koin instance
- **Multi-tenant applications** - Different tenants with different configurations
- **Plugin/module systems** - Independent modules with their own dependencies
- **Embedded Ktor servers** - Multiple Ktor instances in the same JVM

## Basic Setup

Install `KoinIsolated` instead of `Koin`:

```kotlin
fun Application.main() {
    install(KoinIsolated) {
        slf4jLogger()
        modules(appModule)
    }
}
```

## Global vs Isolated Context

### Global Context (default)

```kotlin
// Uses GlobalContext - shared across the application
install(Koin) {
    modules(appModule)
}

// Can access Koin anywhere via GlobalContext
val service = GlobalContext.get().get<UserService>()
```

### Isolated Context

```kotlin
// Uses isolated context - not accessible via GlobalContext
install(KoinIsolated) {
    modules(appModule)
}

// GlobalContext.get() will NOT return this Koin instance
// Only accessible within the Ktor application scope
```

:::warning
When using `KoinIsolated`, you cannot access Koin via `GlobalContext`. All injection must happen within the Ktor application scope using `inject()` or `get()`.
:::

## Complete Example

```kotlin
val appModule = module {
    singleOf(::UserRepository)
    singleOf(::UserService)

    requestScope {
        scopedOf(::RequestLogger)
    }
}

fun Application.main() {
    // Install Koin in isolated mode
    install(KoinIsolated) {
        slf4jLogger()
        modules(appModule)
    }

    // Injection works within Application scope
    val userService by inject<UserService>()

    routing {
        get("/users/{id}") {
            val logger = call.scope.get<RequestLogger>()
            val id = call.parameters["id"]!!

            logger.log("Fetching user $id")
            val user = userService.getUser(id)

            call.respond(user)
        }
    }
}
```

## With DI Bridge

The isolated context also supports the Ktor DI Bridge:

```kotlin
fun Application.main() {
    // Ktor DI - Infrastructure
    val database = Database(environment.config)
    dependencies {
        provide<Database> { database }
    }

    // Koin Isolated with bridge
    install(KoinIsolated) {
        slf4jLogger()

        bridge {
            koinToKtor()  // Allow Koin to resolve from Ktor DI
        }

        modules(appModule)
    }

    routing {
        userRoutes()
    }
}

val appModule = module {
    // Can inject Database from Ktor DI via bridge
    singleOf(::UserRepository)
    singleOf(::UserService)
}
```

## Testing with Isolated Context

Isolated context is particularly useful for testing:

```kotlin
class UserServiceTest {
    @Test
    fun `test user endpoint`() = testApplication {
        application {
            // Each test gets its own isolated Koin instance
            install(KoinIsolated) {
                modules(testModule)
            }
            configureRouting()
        }

        client.get("/users/123").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}

val testModule = module {
    single<UserRepository> { MockUserRepository() }
    singleOf(::UserService)
}
```

### Parallel Test Execution

With isolated context, tests can run in parallel without interference:

```kotlin
class ParallelTests {
    @Test
    fun `test A`() = testApplication {
        application {
            install(KoinIsolated) {
                modules(moduleA)  // Own isolated instance
            }
        }
        // ...
    }

    @Test
    fun `test B`() = testApplication {
        application {
            install(KoinIsolated) {
                modules(moduleB)  // Different isolated instance
            }
        }
        // ...
    }
}
```

## Multiple Ktor Servers

Run multiple Ktor servers with independent Koin instances:

```kotlin
fun main() {
    // Server 1 - User Service
    val userServer = embeddedServer(Netty, port = 8080) {
        install(KoinIsolated) {
            modules(userServiceModule)
        }
        userRouting()
    }

    // Server 2 - Order Service (different Koin instance)
    val orderServer = embeddedServer(Netty, port = 8081) {
        install(KoinIsolated) {
            modules(orderServiceModule)
        }
        orderRouting()
    }

    // Both servers have independent Koin containers
    userServer.start(wait = false)
    orderServer.start(wait = true)
}
```

## Lifecycle

The isolated Koin instance follows the Ktor application lifecycle:

```kotlin
fun Application.main() {
    install(KoinIsolated) {
        slf4jLogger()
        modules(appModule)
    }

    // Monitor Koin lifecycle
    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Isolated Koin started")
    }

    environment.monitor.subscribe(KoinApplicationStopped) {
        log.info("Isolated Koin stopped")
    }
}
```

## Accessing the Isolated Koin Instance

Within the Ktor application, you can access the isolated Koin instance:

```kotlin
fun Application.main() {
    install(KoinIsolated) {
        modules(appModule)
    }

    // Access Koin instance from Application
    val koin = getKoin()

    // Or via plugin attribute
    val koinApp = attributes[KoinPluginKey]
}
```

## When NOT to Use Isolated Context

- **Single Ktor application** - Global context is simpler
- **Shared dependencies across modules** - Global context allows sharing
- **Background jobs accessing Koin** - They need GlobalContext

## Best Practices

1. **Use for testing** - Isolated context prevents test interference
2. **Use for multi-tenant** - Each tenant can have different configurations
3. **Avoid for simple apps** - Global context is simpler for most use cases
4. **Document the choice** - Make it clear why isolated context is used

## See Also

- **[Ktor Integration](/docs/reference/koin-ktor/ktor)** - Main Ktor documentation
- **[Context Isolation](/docs/reference/koin-core/context-isolation)** - Core isolation concepts
- **[Testing](/docs/reference/koin-test/testing)** - Testing with Koin
