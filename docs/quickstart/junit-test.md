---
title: JUnit Tests
---

> This tutorial lets you test a Kotlin application and use Koin inject and retrieve your components.

:::note
update - 2025-01-28
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/kotlin)
:::

## Gradle Setup

First, add the Koin dependency like below:

```groovy
dependencies {
    // Koin testing tools
    testImplementation "io.insert-koin:koin-test:$koin_version"
    // Needed JUnit version
    testImplementation "io.insert-koin:koin-test-junit4:$koin_version"
}
```

## Declared dependencies

We reuse the `koin-core` getting-started project, to use the koin module:

```kotlin
val appModule = module {
    single<UserApplication>()
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

## Verifying your Modules

The simplest way to test your Koin configuration is to verify your modules. The `verify()` function performs a dry-run check to ensure all dependencies can be resolved:

```kotlin
class ModuleVerificationTest : AutoCloseKoinTest() {

    @Test
    fun verifyModules() {
        appModule.verify()
    }
}
```

This test will fail if any dependency definitions are invalid or if any required dependencies are missing.

## Writing Tests with KoinTestRule

To write tests that inject dependencies, extend `KoinTest` and use `KoinTestRule`:

```kotlin
class UserAppTest : KoinTest {

    val userService by inject<UserService>()
    val userRepository by inject<UserRepository>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(appModule)
    }

    @Test
    fun `test user service`() {
        // Load users via service
        userService.loadUsers()

        // Verify user can be found
        val user = userService.getUserOrNull("Alice")
        assertNotNull(user)
        assertEquals("Alice", user?.name)
    }
}
```

> We use the `KoinTestRule` to start/stop our Koin context for each test

## Mocking Dependencies

You can mock dependencies in your tests using `declareMock`. This replaces the real implementation with a mock:

```kotlin
class UserMockTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger(Level.DEBUG)
        modules(appModule)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun `mock test`() {
        // Declare a mock for UserRepository
        val repository = declareMock<UserRepository> {
            given(findUserOrNull(anyString())).willReturn(
                User("Mock", "mock@example.com")
            )
        }

        // Use the application with mocked repository
        getKoin().get<UserApplication>().sayHello("Mock")

        // Verify the mock was called
        Mockito.verify(repository, times(1)).findUserOrNull(anyString())
    }
}
```

The `MockProviderRule` configures Mockito as the mocking framework, and `declareMock` replaces the real `UserRepository` with a mock that returns controlled data.

## Key Testing Concepts

| Concept | Description |
|---------|-------------|
| `KoinTest` | Interface to extend for Koin testing support |
| `AutoCloseKoinTest` | Auto-closes Koin after each test |
| `KoinTestRule` | JUnit rule to start/stop Koin context |
| `MockProviderRule` | Configures the mocking framework |
| `verify()` | Validates module configuration without running |
| `declareMock<T>()` | Replaces a definition with a mock |
| `by inject<T>()` | Lazily injects a dependency in tests |

## See Also

- **[Testing Reference](/docs/reference/koin-test/testing)** - Complete testing documentation
- **[Module Verification](/docs/reference/koin-test/verify)** - verify() and checkModules() details
