---
title: Testing
---

# Testing Ktor with Koin

Best practices for testing Ktor applications that use Koin for dependency injection.

## Test Configuration

### Basic Test Setup

```kotlin
class UserServiceTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testModule)
    }

    private val userService: UserService by inject()

    @Test
    fun `should return user`() {
        val user = userService.getUser("123")
        assertNotNull(user)
    }
}

val testModule = module {
    single<UserRepository> { MockUserRepository() }
    singleOf(::UserService)
}
```

## Testing with Ktor testApplication

```kotlin
class ApplicationTest {
    @Test
    fun `test hello endpoint`() = testApplication {
        application {
            install(Koin) {
                modules(testModule)
            }
            configureRouting()
        }

        client.get("/hello?name=Test").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertContains(bodyAsText(), "Test")
        }
    }
}
```

## Using Isolated Context for Tests

Each test gets its own isolated Koin instance:

```kotlin
class UserRoutesTest {
    @Test
    fun `test user endpoint`() = testApplication {
        application {
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
```

### Parallel Test Execution

With isolated context, tests can run in parallel without interference:

```kotlin
class ParallelTests {
    @Test
    fun `test A`() = testApplication {
        application {
            install(KoinIsolated) {
                modules(moduleA)
            }
        }
        // ...
    }

    @Test
    fun `test B`() = testApplication {
        application {
            install(KoinIsolated) {
                modules(moduleB)
            }
        }
        // ...
    }
}
```

## Module Verification

Verify modules at compile time with annotations or at test time:

```kotlin
class ModuleVerificationTest : KoinTest {
    @Test
    fun `verify all modules`() {
        appModule.verify()
    }
}
```

### With Extra Types

```kotlin
@Test
fun `verify modules with extra types`() {
    appModule.verify(
        extraTypes = listOf(
            ApplicationCall::class,
            Application::class
        )
    )
}
```

## Mocking Dependencies

### Using Test Modules

```kotlin
val productionModule = module {
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::UserService)
}

val testModule = module {
    single<UserRepository> { MockUserRepository() }
    singleOf(::UserService)
}
```

### Using Mockk

```kotlin
class UserServiceTest : KoinTest {
    private val mockRepository = mockk<UserRepository>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single { mockRepository }
            singleOf(::UserService)
        })
    }

    @Test
    fun `should call repository`() {
        val userService: UserService by inject()

        every { mockRepository.findById("123") } returns User("123", "Test")

        val user = userService.getUser("123")

        verify { mockRepository.findById("123") }
        assertEquals("Test", user?.name)
    }
}
```

## Testing Request Scopes

```kotlin
class RequestScopeTest {
    @Test
    fun `test request scoped component`() = testApplication {
        application {
            install(Koin) {
                modules(module {
                    singleOf(::UserService)
                    requestScope {
                        scopedOf(::RequestLogger)
                    }
                })
            }

            routing {
                get("/test") {
                    val logger = call.scope.get<RequestLogger>()
                    logger.log("Test message")
                    call.respondText("OK")
                }
            }
        }

        client.get("/test").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
```

## Testing with DI Bridge

```kotlin
class BridgeTest {
    @Test
    fun `test with bridge`() = testApplication {
        application {
            dependencies {
                provide<Database> { MockDatabase() }
            }

            install(Koin) {
                bridge {
                    koinToKtor()
                }
                modules(appModule)
            }

            configureRouting()
        }

        client.get("/users").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
```

## Complete Test Example

```kotlin
class UserApiTest : KoinTest {

    @Test
    fun `should return all users`() = testApplication {
        application {
            install(KoinIsolated) {
                modules(testModule)
            }

            routing {
                val userService by inject<UserService>()

                get("/api/users") {
                    call.respond(userService.getAllUsers())
                }
            }
        }

        client.get("/api/users").apply {
            assertEquals(HttpStatusCode.OK, status)
            val users = Json.decodeFromString<List<User>>(bodyAsText())
            assertEquals(2, users.size)
        }
    }

    @Test
    fun `should return user by id`() = testApplication {
        application {
            install(KoinIsolated) {
                modules(testModule)
            }

            routing {
                val userService by inject<UserService>()

                get("/api/users/{id}") {
                    val id = call.parameters["id"]!!
                    val user = userService.getUser(id)
                        ?: return@get call.respond(HttpStatusCode.NotFound)
                    call.respond(user)
                }
            }
        }

        client.get("/api/users/1").apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        client.get("/api/users/999").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }
}

val testModule = module {
    single<UserRepository> {
        MockUserRepository(
            listOf(
                User("1", "Alice", "alice@example.com"),
                User("2", "Bob", "bob@example.com")
            )
        )
    }
    singleOf(::UserService)
}
```

## Best Practices

1. **Use isolated context** - Prevents test interference
2. **Create test modules** - Override production dependencies with mocks
3. **Verify modules** - Catch configuration errors early
4. **Clean up** - Tests using global context should stop Koin after
5. **Parallel safety** - Use `KoinIsolated` for parallel test execution

## See Also

- **[Koin for Ktor](/docs/reference/koin-ktor/ktor)** - Main Ktor documentation
- **[Isolated Context](/docs/reference/koin-ktor/ktor-isolated)** - Isolated Koin instances
- **[Testing](/docs/reference/koin-test/testing)** - Core testing documentation
