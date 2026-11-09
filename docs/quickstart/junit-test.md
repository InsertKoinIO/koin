---
title: JUnit Tests
description: Test the Kotlin tutorial app with Koin Test and JUnit 4, start Koin with KoinTestRule, verify the module and replace a dependency with a mock.
---

In this tutorial you write JUnit 4 tests for the app from the [Kotlin tutorial](/docs/quickstart/kotlin). You learn how to start Koin for each test with `KoinTestRule`, check a module with `verify()`, and replace a dependency with a Mockito mock. It takes about 10 minutes. Do the Kotlin tutorial first, or start from its sample.

This tutorial follows the Compiler Plugin DSL app. All the testing APIs are described in [Unit Testing](/docs/reference/koin-test/testing).

## Get the code

The tests are in the [koin-getting-started/kotlin](https://github.com/InsertKoinIO/koin-getting-started/tree/main/kotlin) sample, under `app/src/test`. Clone the repository and run them from the `kotlin` folder:

```bash
./gradlew test
```

## Gradle setup

The sample adds `koin-test`, `koin-test-junit4` and Mockito as test dependencies:

```toml
# gradle/libs.versions.toml
[versions]
koin = "<KOIN_VERSION>"
mockito = "<MOCKITO_VERSION>"

[libraries]
koin-test = { module = "io.insert-koin:koin-test", version.ref = "koin" }
koin-test-junit4 = { module = "io.insert-koin:koin-test-junit4", version.ref = "koin" }
mockito = { module = "org.mockito:mockito-inline", version.ref = "mockito" }
```

```kotlin
// app/build.gradle.kts
dependencies {
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.mockito)
}
```

`koin-test` does not depend on a mocking library, so you add the one you use. For JUnit 5, use `koin-test-junit5` instead (see [Unit Testing](/docs/reference/koin-test/testing)).

## The module under test

The tests load the module from the Kotlin tutorial:

```kotlin
val appModule = module {
    single<UserApplication>()
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

Each test starts Koin with this module, so it runs against the same graph as the app.

## Let's verify the module

`verify()` checks that every constructor parameter in the module has a definition, without creating any instance:

```kotlin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.verify.verify

class ModuleVerificationTest : AutoCloseKoinTest() {

    @Test
    fun verifyModules() {
        appModule.verify()
    }
}
```

The test fails on a missing or invalid definition. The Compiler Plugin already runs this check at build time, so `verify()` matters most for modules the plugin does not cover. See [Verifying your Configuration](/docs/reference/koin-test/verify) to decide whether you need it.

## Let's start Koin in a test

Implement `KoinTest` and add a `KoinTestRule`. The rule starts Koin before each test and stops it after:

```kotlin
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule

class HelloMockTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appModule)
    }
}
```

`KoinTest` gives the test class `get()` and `by inject()`, for example `val userService: UserService by inject()`. Starting a fresh container per test keeps instances from leaking between tests.

## Let's mock a dependency

Add a `MockProviderRule` to tell Koin how to build mocks, then replace `UserRepository` with `declareMock`:

```kotlin
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.times

class HelloMockTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appModule)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz -> Mockito.mock(clazz.java) }

    @Test
    fun `mock test`() {
        val service = declareMock<UserRepository> {
            given(findUserOrNull(anyString())).willReturn(User("Mock", "mock@example.com"))
        }

        getKoin().get<UserApplication>().sayHello("Mock")
        Mockito.verify(service, times(1)).findUserOrNull(anyString())
    }
}
```

`declareMock` replaces the `UserRepository` definition for this test only. `UserApplication` and `UserService` are real, so the test checks that the call goes through `UserService` down to the repository once.

## Next steps

- [Unit Testing](/docs/reference/koin-test/testing): every testing API, JUnit 5 and overriding definitions.
- [Verifying your Configuration](/docs/reference/koin-test/verify): `verify()` options and when the Compiler Plugin replaces it.
- [Instrumented testing](/docs/reference/koin-android/instrumented-testing): test Android apps that use Koin.
- [Ktor testing](/docs/reference/koin-ktor/ktor-testing): test Ktor routes with Koin.
- [Tooling](/docs/tooling): IDE plugin and monitoring tools for Koin.
