---
title: Isolated Context in Ktor
description: Run Koin inside a Ktor application without the global context using the KoinIsolated plugin, for tests and several servers in one JVM.
---

This page covers the `KoinIsolated` plugin, which starts Koin for one Ktor application without registering it in the global context. Each application then has its own container. What an isolated context is, and when to use one, is explained in [Context Isolation](/docs/reference/koin-core/context-isolation). The regular `Koin` plugin is described in [Koin for Ktor](/docs/reference/koin-ktor/ktor).

## Installing KoinIsolated

Install `KoinIsolated` instead of `Koin`. The configuration block is the same:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.ktor.plugin.KoinIsolated

fun Application.module() {
    install(KoinIsolated) {
        slf4jLogger()
        modules(appModule)
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.ktor.plugin.KoinIsolated
import org.koin.plugin.module.dsl.withConfiguration

fun Application.module() {
    install(KoinIsolated) {
        slf4jLogger()
        withConfiguration<KoinUserApplication>()
    }
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.ktor.plugin.KoinIsolated

fun Application.module() {
    install(KoinIsolated) {
        slf4jLogger()
        modules(appModule)
    }
}
```

</TabItem>
</Tabs>

## What changes compared to Koin

| | `install(Koin)` | `install(KoinIsolated)` |
|---|---|---|
| Registered in the global context | Yes | No |
| `inject()` / `get()` on `Application`, `Route`, `Routing`, `ApplicationCall` | Yes | Yes, from the application's instance |
| Request scope and `call.scope` | Yes | Yes |
| Lifecycle events, close on `ApplicationStopping` | Yes | Yes |
| `bridge { }` to Ktor DI | Yes | Not applied in Koin 4.2 |
| `KoinComponent` classes, `KoinPlatform.getKoin()` | See this instance | Don't see this instance |

The Ktor extensions read the Koin instance stored in the application attributes, so injection in routes works the same:

```kotlin
fun Application.module() {
    install(KoinIsolated) {
        modules(appModule)
    }

    val userService by inject<UserService>()
    val koin = getKoin() // this application's Koin instance

    routing {
        get("/users/{id}") {
            val logger = call.scope.get<RequestLogger>()
            call.respond(userService.getUser(call.parameters["id"]!!))
        }
    }
}
```

:::warning
Code outside Ktor's receivers can't reach the isolated instance. A class implementing `KoinComponent`, or a call to `KoinPlatform.getKoin()`, uses the global context and fails with "KoinApplication has not been started". Give your classes their dependencies through constructor parameters, declared in the isolated modules. For an interop entry point that must retrieve dependencies itself, override `getKoin()` as shown in [Context Isolation](/docs/reference/koin-core/context-isolation).
:::

## Several servers in one JVM

Each embedded server gets its own container:

```kotlin
fun main() {
    val userServer = embeddedServer(Netty, port = 8080) {
        install(KoinIsolated) { modules(userServiceModule) }
        userRouting()
    }

    val orderServer = embeddedServer(Netty, port = 8081) {
        install(KoinIsolated) { modules(orderServiceModule) }
        orderRouting()
    }

    userServer.start(wait = false)
    orderServer.start(wait = true)
}
```

With `install(Koin)`, the second server would stop the global Koin of the first one, since the plugin stops a running global instance before it starts.

## Tests

Each `testApplication` that installs `KoinIsolated` has its own container, so tests don't share state through the global context and can run in parallel. Test setup is covered in [Ktor Testing](/docs/reference/koin-ktor/ktor-testing).

## When to keep the global context

Stay with `install(Koin)` when one Ktor application runs in the process and other entry points of that process (a scheduled job started from `main()`, for example) must use the same container, or when you need the Ktor DI bridge.

## Related

- [Context Isolation](/docs/reference/koin-core/context-isolation): the isolated context concept and a custom `KoinComponent`.
- [Koin for Ktor](/docs/reference/koin-ktor/ktor): the `Koin` plugin and injection in routes.
- [Ktor Testing](/docs/reference/koin-ktor/ktor-testing): test Ktor applications with Koin.
- [Ktor DI Bridge](/docs/reference/koin-ktor/ktor-bridge): share dependencies with Ktor DI.
