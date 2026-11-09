---
title: Koin for Ktor
description: Install the Koin plugin in a Ktor server, inject dependencies in Application, Route and call handlers, and react to Koin lifecycle events.
---

This page shows how to add Koin to a Ktor server application: install the `Koin` plugin, inject dependencies in your routes, load modules from feature functions, and listen to Koin lifecycle events. You should know how to declare [definitions](/docs/reference/koin-core/definitions) and [modules](/docs/reference/koin-core/modules). For a step-by-step project, see the [Ktor tutorial](/docs/quickstart/ktor).

## Setup

```kotlin
// build.gradle.kts
dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j) // optional, SLF4J logger
}
```

Catalog entries and versions are in [Gradle Setup](/docs/setup/gradle). The `koin-ktor` plugin targets Ktor 3.

## Installing the Koin plugin

Install `Koin` in your application module. Its configuration block is the same as `startKoin { }`:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserService>()
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.plugin.module.dsl.withConfiguration

@Module
@ComponentScan("com.example")
@Configuration
class AppModule

@KoinApplication
object KoinUserApplication

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        withConfiguration<KoinUserApplication>()
    }
}
```

`withConfiguration<T>()` loads the modules assembled by the `@KoinApplication` class.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val appModule = module {
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::UserService)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
```

</TabItem>
</Tabs>

What the plugin does:

- **Global start**: it starts Koin in the global context, like `startKoin`. If a global Koin instance is already running (for example after a Ktor auto-reload), the plugin stops it first. To keep Koin out of the global context, use [`KoinIsolated`](/docs/reference/koin-ktor/ktor-isolated).
- **Eager instances**: definitions marked `createdAtStart` are created at install time.
- **Request scope**: it opens a Koin scope for each call. See [Request Scopes](/docs/reference/koin-ktor/ktor-scopes).
- **Shutdown**: when Ktor raises `ApplicationStopping`, the plugin closes the Koin instance.

Logging, properties and other options of the configuration block are described in [Starting Koin](/docs/reference/koin-core/starting-koin).

## Injecting dependencies

`inject()` (lazy) and `get()` (immediate) are extension functions from `org.koin.ktor.ext` on `Application`, `Route`, `Routing` and `ApplicationCall`:

```kotlin
import org.koin.ktor.ext.inject

fun Application.module() {
    install(Koin) { modules(appModule) }

    val userService by inject<UserService>()

    routing {
        get("/users") {
            call.respond(userService.getAllUsers())
        }
    }
}
```

In a route extension function:

```kotlin
fun Route.customerRoutes() {
    val customerService by inject<CustomerService>()

    get("/customers/{id}") {
        val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        call.respond(customerService.getCustomer(id))
    }
}
```

Inside a handler, resolve from the call:

```kotlin
get("/users/{id}") {
    val userService = call.get<UserService>()
    call.respond(userService.getUser(call.parameters["id"]!!))
}
```

All four receivers resolve from the Koin instance stored in the `Application`: the global one with `Koin`, the isolated one with `KoinIsolated`. They accept a `qualifier` and `parameters`, like the core functions (see [Injection](/docs/reference/koin-core/injection)). `getProperty(key)` and `getProperty(key, defaultValue)` read Koin properties from the same receivers.

## Loading modules from feature functions

Once the plugin is installed, a feature function can add its own definitions:

```kotlin
import org.koin.ktor.plugin.koinModule
import org.koin.ktor.plugin.koinModules

fun Application.customerFeature() {
    koinModule {
        singleOf(::CustomerRepository)
        singleOf(::CustomerService)
    }

    routing { customerRoutes() }
}

fun Application.orderFeature() {
    koinModules(orderDataModule, orderServiceModule)

    routing { orderRoutes() }
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(coreModule)
    }
    customerFeature()
    orderFeature()
}
```

`koinModule { }` declares a module inline, `koinModules(...)` loads existing `Module` values. Both fail with "Koin plugin is not installed properly" if `install(Koin)` was not called first.

## Lifecycle events

The plugin raises three events on the application monitor:

| Event | Raised |
|-------|--------|
| `KoinApplicationStarted` | When the plugin is installed and Koin is started |
| `KoinApplicationStopPreparing` | On `ApplicationStopping`, before Koin is closed |
| `KoinApplicationStopped` | After Koin is closed |

```kotlin
fun Application.module() {
    monitor.subscribe(KoinApplicationStarted) { koinApp ->
        log.info("Koin started")
    }
    monitor.subscribe(KoinApplicationStopped) {
        log.info("Koin stopped")
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
```

`KoinApplicationStarted` is raised during `install(Koin)`. Subscribe before installing the plugin, or the handler never runs. Each handler receives the `KoinApplication`.

## Ktor's own DI

Ktor 3.4 has a built-in dependency injection plugin. Koin can share dependencies with it in both directions: see [Ktor DI Bridge](/docs/reference/koin-ktor/ktor-bridge).

## Related

- [Request Scopes](/docs/reference/koin-ktor/ktor-scopes): instances that live for one HTTP call.
- [Ktor DI Bridge](/docs/reference/koin-ktor/ktor-bridge): resolve between Koin and Ktor DI.
- [Isolated Context in Ktor](/docs/reference/koin-ktor/ktor-isolated): Koin without the global context.
- [Ktor Testing](/docs/reference/koin-ktor/ktor-testing): test routes with Koin.
- [Ktor tutorial](/docs/quickstart/ktor) and [Ktor tutorial with annotations](/docs/quickstart/ktor-annotations): build a Ktor app with Koin step by step.
