---
title: Ktor Testing
description: Test Ktor routes that use Koin with testApplication, replace definitions with test modules, and run tests in parallel with KoinIsolated.
---

This page covers testing a Ktor server that uses Koin: running your routes with `testApplication`, replacing definitions with test modules, and isolating tests from each other. It assumes you know how to [install Koin in Ktor](/docs/reference/koin-ktor/ktor). For tests of plain classes, with no HTTP layer, use the techniques of [Unit Testing](/docs/reference/koin-test/testing).

## Dependencies

```kotlin
dependencies {
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("io.insert-koin:koin-test")
    testImplementation("io.insert-koin:koin-test-junit4") // or koin-test-junit5
}
```

## Run routes with testApplication

`install(Koin)` works inside `testApplication` as in production. Install it with your test modules, then add your routes:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class UserRoutesTest {

    @Test
    fun `returns a user`() = testApplication {
        application {
            install(Koin) {
                modules(appModule, testModule)
            }
            configureRouting()
        }

        val response = client.get("/users/1")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
import org.koin.plugin.module.dsl.withConfiguration

class UserRoutesTest {

    @Test
    fun `returns a user`() = testApplication {
        application {
            install(Koin) {
                withConfiguration<MyApp>()
                modules(testModule)
            }
            configureRouting()
        }

        val response = client.get("/users/1")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
```

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
class UserRoutesTest {

    @Test
    fun `returns a user`() = testApplication {
        application {
            install(Koin) {
                modules(appModule, testModule)
            }
            configureRouting()
        }

        val response = client.get("/users/1")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
```

</TabItem>
</Tabs>

```kotlin
val testModule = module {
    single<UserRepository> { InMemoryUserRepository(User("1", "Alice")) }
}
```

The test module is loaded last, so its `UserRepository` replaces the production one (see [Definitions](/docs/reference/koin-core/definitions#overriding-a-definition)). A lambda definition like this one works in every style.

Routes resolve dependencies with `inject()` and `get()` from `org.koin.ktor.ext`, as in production. When `testApplication` ends, Ktor stops the application and the Koin plugin closes its container.

## Reuse your application module

If your production `Application.module()` installs Koin, call it in the test and add overrides with `koinModules()`:

```kotlin
// production
fun Application.module() {
    install(Koin) {
        modules(appModule)
    }
    configureRouting()
}

// test
@Test
fun `returns a user`() = testApplication {
    application {
        module()
        koinModules(testModule)
    }

    assertEquals(HttpStatusCode.OK, client.get("/users/1").status)
}
```

`koinModules()` loads modules into the container created by `install(Koin)`. Ktor plugins can only be installed once, so a second `install(Koin)` in the test would fail.

`koinModules()` loads the overrides after `install(Koin)`. A definition created at install time (for example with `createdAtStart`) keeps the production dependencies.

## Isolate tests

`install(Koin)` starts Koin in the global context. If a container is already running, the plugin stops it first. Tests in sequence are fine, but two tests that run at the same time share the global context and break each other.

Extend `AutoCloseKoinTest` to stop Koin after each test, even when a test fails before the application shuts down:

```kotlin
class UserRoutesTest : AutoCloseKoinTest() {
    // tests
}
```

For parallel tests, install `KoinIsolated` instead. Each application gets its own container, outside the global context:

```kotlin
@Test
fun `returns a user`() = testApplication {
    application {
        install(KoinIsolated) {
            modules(appModule, testModule)
        }
        configureRouting()
    }

    assertEquals(HttpStatusCode.OK, client.get("/users/1").status)
}
```

With `KoinIsolated`, resolve dependencies through the Ktor extensions (`inject()`, `get()`, `call.scope`). A `KoinComponent` or `GlobalContext.get()` does not see the isolated container. See [Isolated Context in Ktor](/docs/reference/koin-ktor/ktor-isolated).

## Mock a dependency

Put the mock in the test module:

```kotlin
@Test
fun `returns 404 for an unknown user`() = testApplication {
    val repository = mockk<UserRepository> {
        every { findById(any()) } returns null
    }

    application {
        install(Koin) {
            modules(appModule, module { single { repository } })
        }
        configureRouting()
    }

    assertEquals(HttpStatusCode.NotFound, client.get("/users/999").status)
    verify { repository.findById("999") }
}
```

`declareMock` (see [Mocking with declareMock](/docs/reference/koin-test/testing#mocking-with-declaremock)) also works with `install(Koin)`, since it uses the global context. It does not work with `KoinIsolated`.

## Request scope

Definitions declared in `requestScope { }` are created for each call and closed when the response is sent (see [Request Scopes in Ktor](/docs/reference/koin-ktor/ktor-scopes)). Test them through a request:

```kotlin
@Test
fun `each call gets its own request logger`() = testApplication {
    application {
        install(Koin) {
            modules(module {
                requestScope {
                    scoped { RequestLogger() }
                }
            })
        }
        routing {
            get("/id") {
                call.respondText(call.scope.get<RequestLogger>().id)
            }
        }
    }

    val first = client.get("/id").bodyAsText()
    val second = client.get("/id").bodyAsText()
    assertNotEquals(first, second)
}
```

`call.scope` exists only while a call is processed. Resolving a request-scoped definition outside a route fails.

## Ktor DI bridge

When the [Ktor DI bridge](/docs/reference/koin-ktor/ktor-bridge) is enabled, provide test values with Ktor's `dependencies { }` in the test application, the same way as in production. `koinToKtor()` lets Koin definitions resolve them:

```kotlin
@Test
fun `uses the Ktor-provided database`() = testApplication {
    application {
        dependencies {
            provide<Database> { InMemoryDatabase() }
        }
        install(Koin) {
            bridge { koinToKtor() }
            modules(appModule)
        }
        configureRouting()
    }

    assertEquals(HttpStatusCode.OK, client.get("/users").status)
}
```

## Check your configuration

To check that every definition's dependencies are declared, without starting a server, see [Verifying your Configuration](/docs/reference/koin-test/verify). Types provided by Ktor at runtime, such as `ApplicationCall` in a request scope, go in `extraTypes`.

## Related

- [Koin for Ktor](/docs/reference/koin-ktor/ktor): install the plugin and inject in routes.
- [Isolated Context in Ktor](/docs/reference/koin-ktor/ktor-isolated): `KoinIsolated` and when to use it.
- [Request Scopes in Ktor](/docs/reference/koin-ktor/ktor-scopes): definitions bound to a call.
- [Unit Testing](/docs/reference/koin-test/testing): `KoinTest`, overrides and `declareMock`.
- [Verifying your Configuration](/docs/reference/koin-test/verify): check modules in a test.
