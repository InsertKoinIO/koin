---
title: DI Bridge
---

# Ktor DI Bridge

Koin 4.2+ provides seamless integration with **Ktor 3.4+** built-in dependency injection system through a configurable bridge.

:::info Experimental Feature
The Ktor DI Bridge is an experimental feature that enables bidirectional dependency resolution between Koin and Ktor DI.
:::

## Bridge Configuration

Use the `bridge { }` DSL to enable bidirectional dependency resolution:

```kotlin
fun Application.module() {
    install(Koin) {
        slf4jLogger()

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

## Bridge Options

| Option | Description |
|--------|-------------|
| `ktorToKoin()` | Allows Ktor's `by dependencies` delegate to resolve from Koin |
| `koinToKtor()` | Allows Koin's `inject()` and `get()` to resolve from Ktor DI |

## Using ktorToKoin()

Resolve Koin dependencies using Ktor's `by dependencies` delegate:

```kotlin
fun Application.module() {
    install(Koin) {
        bridge {
            ktorToKoin()
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

## Using koinToKtor()

Resolve Ktor DI dependencies using Koin's `inject()`:

```kotlin
fun Application.module() {
    install(Koin) {
        bridge {
            koinToKtor()
        }
        modules(appModule)
    }

    // Declare in Ktor DI
    dependencies {
        provide<DatabaseConnection> { DatabaseConnectionImpl() }
    }

    routing {
        get("/data") {
            // Resolve Ktor DI dependency using Koin
            val database: DatabaseConnection by inject()
            call.respondText(database.query())
        }
    }
}
```

## Full Bidirectional Example

Enable both directions for maximum flexibility:

```kotlin
fun Application.module() {
    install(Koin) {
        slf4jLogger()

        bridge {
            ktorToKoin()
            koinToKtor()
        }

        modules(module {
            single<HelloService> { HelloServiceImpl() }
        })
    }

    dependencies {
        provide<KtorSpecificService> { KtorSpecificServiceImpl() }
    }

    routing {
        // Using Koin's inject() for both
        get("/mixed-koin") {
            val helloService: HelloService by inject()        // From Koin
            val ktorService: KtorSpecificService by inject()  // From Ktor DI
            call.respondText("${helloService.sayHello()} - ${ktorService.process()}")
        }

        // Using Ktor's dependencies delegate for both
        get("/mixed-ktor") {
            val helloService: HelloService by dependencies        // From Koin
            val ktorService: KtorSpecificService by dependencies  // From Ktor DI
            call.respondText("${helloService.sayHello()} - ${ktorService.process()}")
        }
    }
}
```

## Architecture Pattern

Separate infrastructure from application logic:

- **Ktor DI** - Framework-level dependencies (Database, Configuration, Infrastructure)
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

    // Koin - Application layer
    install(Koin) {
        slf4jLogger()

        bridge {
            koinToKtor()  // Koin can resolve infrastructure deps
        }

        modules(appModule)
    }
}

val appModule = module {
    // These can inject Database from Ktor DI via bridge
    singleOf(::CustomerRepository)
    singleOf(::OrderRepository)
    singleOf(::CustomerService)
}
```

## Best Practices

1. **Infrastructure in Ktor DI** - Database connections, configuration, external clients
2. **Business logic in Koin** - Repositories, services, use cases
3. **Enable only needed direction** - Use `koinToKtor()` only, not both, unless necessary
4. **Document the boundary** - Make it clear which system owns which dependencies

## With Isolated Context

The bridge also works with `KoinIsolated`:

```kotlin
fun Application.module() {
    dependencies {
        provide<Database> { Database(environment.config) }
    }

    install(KoinIsolated) {
        slf4jLogger()

        bridge {
            koinToKtor()
        }

        modules(appModule)
    }
}
```

## See Also

- **[Koin for Ktor](/docs/reference/koin-ktor/ktor)** - Main Ktor documentation
- **[Isolated Context](/docs/reference/koin-ktor/ktor-isolated)** - Isolated Koin instances
