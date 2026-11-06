---
title: Ktor DI Bridge
description: Let Koin resolve dependencies declared in Ktor's built-in DI, and let Ktor's dependencies delegate resolve Koin definitions.
---

Ktor 3.4 ships its own dependency injection plugin (`dependencies { provide<T> { } }` and `by dependencies`). This page shows how to connect it to Koin with the `bridge { }` option of the Koin plugin, in one or both directions. You need the Koin plugin installed as in [Koin for Ktor](/docs/reference/koin-ktor/ktor).

The bridge is available since Koin 4.2. Its functions are marked `@KoinExperimentalAPI`.

## Configuring the bridge

```kotlin
fun Application.module() {
    install(Koin) {
        slf4jLogger()
        bridge {
            ktorToKoin()
            koinToKtor()
        }
        modules(appModule)
    }

    dependencies {
        provide<KtorSpecificService> { KtorSpecificServiceImpl() }
    }
}
```

| Option | Effect |
|--------|--------|
| `ktorToKoin()` | Ktor's `by dependencies` can resolve Koin definitions |
| `koinToKtor()` | Koin's `get()`, `inject()` and constructor injection can resolve Ktor DI dependencies |

Enable only the direction you use.

## Ktor resolving from Koin

With `ktorToKoin()`, the plugin installs Ktor's `DI` plugin with a dependency map backed by Koin:

```kotlin
fun Application.module() {
    install(Koin) {
        bridge { ktorToKoin() }
        modules(module {
            single<HelloService> { HelloServiceImpl() }
        })
    }

    routing {
        get("/hello") {
            val helloService: HelloService by dependencies // declared in Koin
            call.respondText(helloService.sayHello())
        }
    }
}
```

A Ktor dependency key with a name maps to a Koin `named()` qualifier with the same value.

## Koin resolving from Ktor DI

With `koinToKtor()`, Koin adds a resolution extension: when a type has no Koin definition, Koin asks Ktor DI for it.

```kotlin
fun Application.module() {
    install(Koin) {
        bridge { koinToKtor() }
        modules(appModule)
    }

    dependencies {
        provide<DatabaseConnection> { DatabaseConnectionImpl() }
    }

    routing {
        get("/data") {
            val database: DatabaseConnection by inject() // declared in Ktor DI
            call.respondText(database.query())
        }
    }
}
```

Koin definitions can also take Ktor DI types as constructor parameters. A Koin qualifier is passed to Ktor as the dependency name. If Ktor DI has no such dependency either, Koin logs a warning and the resolution fails as for any missing definition.

## Splitting infrastructure and application code

A common split keeps framework-level objects in Ktor DI and application classes in Koin, with `koinToKtor()` only:

```kotlin
fun Application.module() {
    // Ktor DI: infrastructure
    val database = Database(environment.config)
    dependencies {
        provide<Database> { database }
        provide<ApplicationConfig> { environment.config }
    }

    // Koin: application classes
    install(Koin) {
        slf4jLogger()
        bridge { koinToKtor() }
        modules(appModule)
    }
}

val appModule = module {
    singleOf(::CustomerRepository) // receives Database from Ktor DI
    singleOf(::CustomerService)
}
```

## Limits

- **Compile-time checks**: the Koin Compiler Plugin does not see Ktor DI declarations. If compile-time safety reports a Ktor DI type as missing, mark it `@Provided`, as described in [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety).
- **KoinIsolated**: in Koin 4.2, the `KoinIsolated` plugin accepts the `bridge { }` block but does not apply it. Use `install(Koin)` when you need the bridge.
- **Experimental**: the bridge API may change in a future release.

## Related

- [Koin for Ktor](/docs/reference/koin-ktor/ktor): install the plugin and inject in routes.
- [Isolated Context in Ktor](/docs/reference/koin-ktor/ktor-isolated): Koin outside the global context.
- [Request Scopes](/docs/reference/koin-ktor/ktor-scopes): per-call instances.
- [Ktor dependency injection](https://ktor.io/docs/server-dependency-injection.html): Ktor's own DI plugin.
