---
title: Request Scopes
description: Declare Ktor request-scoped definitions with requestScope, resolve them with call.scope, and inject the current ApplicationCall.
---

This page covers the request scope of the Koin Ktor plugin: a Koin scope opened for each HTTP call and closed when the response is sent. Use it for objects that hold per-request state, such as a request logger or the current user. How scopes work in general is explained in [Scopes](/docs/reference/koin-core/scopes). You need the plugin installed as in [Koin for Ktor](/docs/reference/koin-ktor/ktor).

## Declaring request-scoped definitions

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
import org.koin.module.requestScope

val appModule = module {
    single<UserRepository>() // shared by all requests

    requestScope {
        scoped<RequestLogger>()
        scoped<UserSessionHandler>()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.ktor.plugin.RequestScope

@Scope(RequestScope::class)
class RequestLogger(private val call: ApplicationCall)

@Scope(RequestScope::class)
class UserSessionHandler(private val call: ApplicationCall)
```

`requestScope { }` is a scope qualified by the `RequestScope` type, so `@Scope(RequestScope::class)` targets the same scope.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
import org.koin.module.requestScope

val appModule = module {
    singleOf(::UserRepository) // shared by all requests

    requestScope {
        scopedOf(::RequestLogger)
        scopedOf(::UserSessionHandler)
    }
}
```

</TabItem>
</Tabs>

## Resolving in a handler

`call.scope` is the request scope of the current call:

```kotlin
routing {
    get("/users/{id}") {
        val logger = call.scope.get<RequestLogger>()
        val userService = call.get<UserService>()

        logger.log("Fetching user")
        call.respond(userService.getUser(call.parameters["id"]!!))
    }
}
```

Within one call, every `call.scope.get<RequestLogger>()` returns the same instance. The next call gets a new one. A request-scoped definition can depend on singletons, since the request scope falls back to the root scope.

`call.scope` throws "Koin Request Scope is not ready" if the call has no scope, for example when neither `Koin` nor `KoinIsolated` is installed.

## Injecting the ApplicationCall

The call is the source of its request scope, so request-scoped classes can take `ApplicationCall` as a constructor parameter without declaring it:

```kotlin
class RequestLogger(private val call: ApplicationCall) {
    fun log(message: String) {
        println("[${call.request.httpMethod.value} ${call.request.path()}] $message")
    }
}

class UserSessionHandler(private val call: ApplicationCall) {
    fun userId(): String? = call.request.headers["X-User-ID"]
}
```

The scope source is described in [Scopes](/docs/reference/koin-core/scopes). `ApplicationCall` is available in the request scope only: a `single` can't receive it.

## Lifecycle

| Step | When |
|------|------|
| Scope created | Ktor `CallSetup`, before routing |
| Scope id | `request_` followed by a unique counter |
| Scope closed | Ktor `ResponseSent`, after the response is sent |

Closing the scope drops all its instances. To run cleanup code, add `onClose` to the definition:

```kotlin
requestScope {
    scoped { RequestMetrics() } onClose { metrics ->
        metrics?.recordDuration()
    }
}
```

`onClose` receives the instance as a nullable value. There is no `onCreate` callback: run initialization in the constructor or the definition lambda. See [Definitions](/docs/reference/koin-core/definitions) for callbacks.

## Pitfalls

- **Request object in a singleton**: don't inject a request-scoped class into a `single`. The singleton is created once and would keep the instance of the first request. Resolve request-scoped objects from `call.scope` in the handler, or pass them as arguments.
- **Background work**: a coroutine launched from a handler that outlives the call must not use `call.scope` after the response is sent: the scope is closed by then.

## Related

- [Scopes](/docs/reference/koin-core/scopes): scope concepts, scope source, linking.
- [Koin for Ktor](/docs/reference/koin-ktor/ktor): install the plugin and inject in routes.
- [Definitions](/docs/reference/koin-core/definitions): `onClose` and other definition options.
- [Ktor Testing](/docs/reference/koin-ktor/ktor-testing): test routes that use request scopes.
