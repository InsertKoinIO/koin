---
title: Unit Testing
description: Start Koin in JVM unit tests with KoinTest, KoinTestRule or KoinTestExtension, override definitions, and replace dependencies with declareMock.
---

This guide shows how to write JVM unit tests with Koin: start and stop a container per test, retrieve instances, override definitions and replace dependencies with mocks. It is the reference for every testing technique; the platform pages ([Android instrumented](/docs/reference/koin-android/instrumented-testing), [Compose](/docs/reference/koin-compose/compose-testing), [Ktor](/docs/reference/koin-ktor/ktor-testing)) only cover what is specific to them. You should know how to [start Koin](/docs/reference/koin-core/starting-koin) and declare [definitions](/docs/reference/koin-core/definitions).

## Dependencies

Add `koin-test` and the artifact for your JUnit version to your test dependencies:

```kotlin
dependencies {
    testImplementation("io.insert-koin:koin-test")
    // pick one
    testImplementation("io.insert-koin:koin-test-junit4")
    testImplementation("io.insert-koin:koin-test-junit5")
}
```

Versions come from the Koin BOM, see [Gradle Setup](/docs/setup/gradle). `koin-test` has no dependency on a mocking library: you plug in Mockito, MockK or any other one (see [Mocking with declareMock](#mocking-with-declaremock)).

## KoinTest

Implement `KoinTest` in your test class. It makes the class a [KoinComponent](/docs/reference/koin-core/koin-component), so `get()` and `by inject()` work directly in the test:

```kotlin
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject

class UserServiceTest : KoinTest {

    // resolved on first access
    private val userService: UserService by inject()

    @Test
    fun `finds a known user`() {
        startKoin { modules(appModule) }

        val repository = get<UserRepository>()

        assertNotNull(userService.findUser("alice"))
        assertSame(repository, userService.repository)

        stopKoin()
    }
}
```

`KoinTest` works with the global Koin context, the one created by `startKoin`. It does not apply to Android instrumented tests, where the `Application` class starts Koin: see [Android Instrumented Testing](/docs/reference/koin-android/instrumented-testing).

## Start and stop Koin for each test

Each test should start its own container and stop it at the end. A container left running makes the next `startKoin` fail with `KoinApplicationAlreadyStartedException`, and leaks instances between tests.

Load the same configuration as your app:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
class UserServiceTest : KoinTest {

    @Before
    fun before() {
        startKoin { modules(appModule) }
    }

    @After
    fun after() {
        stopKoin()
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

Start the `@KoinApplication` class of your app with the typed API:

```kotlin
import org.koin.plugin.module.dsl.startKoin

class UserServiceTest : KoinTest {

    @Before
    fun before() {
        startKoin<MyApp>()
    }

    @After
    fun after() {
        stopKoin()
    }
}
```

To load only some modules, use `module<DataModule>()` or `modules(DataModule::class, CacheModule::class)` inside a plain `startKoin { }`.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
class UserServiceTest : KoinTest {

    @Before
    fun before() {
        startKoin { modules(appModule) }
    }

    @After
    fun after() {
        stopKoin()
    }
}
```

</TabItem>
</Tabs>

To stop Koin without writing `@After`, extend `AutoCloseKoinTest` or implement `ClosingKoinTest`. Both call `stopKoin()` after each test. The JUnit 5 versions live in `org.koin.test.junit5`.

The JUnit rule and extension below do the start and stop for you, and are the shorter option.

## JUnit 4: KoinTestRule

`KoinTestRule` (from `koin-test-junit4`) starts Koin before each test and stops it after:

```kotlin
import org.koin.test.KoinTestRule

class UserServiceTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(appModule)
    }

    private val userService: UserService by inject()

    @Test
    fun `finds a known user`() {
        assertNotNull(userService.findUser("alice"))
    }
}
```

The lambda is a regular `KoinApplication` declaration: everything you write in `startKoin { }` works here. With annotations, load your app with `withConfiguration<MyApp>()`:

```kotlin
import org.koin.plugin.module.dsl.withConfiguration

@get:Rule
val koinTestRule = KoinTestRule.create {
    withConfiguration<MyApp>()
}
```

If a Koin container is already running when the test starts, the rule closes it first. The started instance is available as `koinTestRule.koin`.

## JUnit 5: KoinTestExtension

`KoinTestExtension` (from `koin-test-junit5`) does the same for JUnit 5. Register it with `@JvmField` and `@RegisterExtension`:

```kotlin
import org.koin.test.junit5.KoinTestExtension

class UserServiceTest : KoinTest {

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(appModule)
    }

    private val userService: UserService by inject()

    @Test
    fun `finds a known user`() {
        assertNotNull(userService.findUser("alice"))
    }
}
```

The extension can also pass the `Koin` instance as a test function parameter:

```kotlin
@Test
fun `resolves from the test container`(koin: Koin) {
    assertNotNull(koin.get<UserRepository>())
}
```

## Override definitions for a test

To replace a production definition, load a test module after the production modules. The module loaded last wins:

```kotlin
val testModule = module {
    single<ApiService> { FakeApiService() }
}

@get:Rule
val koinTestRule = KoinTestRule.create {
    modules(appModule, testModule)
}
```

A lambda definition like `single<ApiService> { FakeApiService() }` works in every style, so you can write test modules this way even if your app uses the Compiler Plugin DSL or annotations.

If your app starts Koin with `allowOverride(false)`, mark each test definition with `.override()`. Override rules are explained in [Definitions](/docs/reference/koin-core/definitions#overriding-a-definition).

## Mocking with declareMock

`declareMock<T>()` creates a mock of `T`, registers it in the running container in place of the existing definition, and returns it. Every definition that depends on `T` then receives the mock.

Koin does not create mocks itself. Register a mock provider first:

```kotlin
// JUnit 4, Mockito
@get:Rule
val mockProvider = MockProviderRule.create { clazz ->
    Mockito.mock(clazz.java)
}

// JUnit 4, MockK
@get:Rule
val mockProvider = MockProviderRule.create { clazz ->
    mockkClass(clazz)
}

// JUnit 5
@JvmField
@RegisterExtension
val mockProvider = MockProviderExtension.create { clazz ->
    mockkClass(clazz)
}
```

Then declare the mock in the test, with an optional stubbing block:

```kotlin
import org.koin.test.mock.declareMock

class UserServiceTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appModule)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz -> mockkClass(clazz) }

    private val userService: UserService by inject()

    @Test
    fun `returns the user from the repository`() {
        val repository = declareMock<UserRepository> {
            every { findUser("alice") } returns User("alice")
        }

        assertEquals("alice", userService.findUser("alice")?.name)
        verify { repository.findUser("alice") }
    }
}
```

`declareMock` takes the same options as a definition:

- `qualifier`: replace a [qualified](/docs/reference/koin-core/qualifiers) definition, for example `declareMock<Database>(named("local"))`.
- `secondaryTypes`: also bind the mock to other types, for example `declareMock<UserRepositoryImpl>(secondaryTypes = listOf(UserRepository::class))`.

The mock is held as a single instance in the root scope. To mock a definition inside a scope, call `declareMock` on that scope: `scope.declareMock<CartState>()`.

:::warning
Declare the mock before the first resolution of the classes that use it. A `single` that was already created keeps the dependency it received.
:::

## Declare an instance with declare

When you need a real or hand-written instance instead of a mock, `declare` registers it in the running container:

```kotlin
import org.koin.test.mock.declare

@Test
fun `uses a fixed clock`() {
    declare<Clock> { FixedClock(at = "2026-01-01T00:00:00Z") }

    assertEquals(2026, get<ReportService>().currentYear())
}
```

`declare` overrides any existing definition for the type, accepts a `qualifier`, and returns the declared instance. It uses the global context, so call it after Koin has started.

## Test scoped definitions

Create the scope yourself, resolve from it, then close it:

```kotlin
@Test
fun `cart state lives in its scope`() {
    val scope = getKoin().createScope<CheckoutSession>("checkout-1")

    val state = scope.get<CartState>()
    assertSame(state, scope.get<CartState>())

    scope.close()
}
```

For the scope APIs, see [Scopes](/docs/reference/koin-core/scopes). On Android, scopes bound to an Activity or Fragment are tested with the component itself: see [Android Instrumented Testing](/docs/reference/koin-android/instrumented-testing#scopes).

## Check your configuration

Unit tests exercise behavior. To check that every dependency of every definition is declared, use the [Koin Compiler Plugin](/docs/reference/koin-compiler/compile-safety) at build time, or the `verify()` test API: see [Verifying your Configuration](/docs/reference/koin-test/verify).

## Limits

- Global context: `startKoin`, `KoinTestRule` and `KoinTestExtension` all use the single global context. Tests that start Koin can't run in parallel in the same JVM. For parallel tests, use an [isolated context](/docs/reference/koin-core/context-isolation). `KoinTest`'s `get()` and `inject()` read the global context unless you override `getKoin()`.
- Mock provider: `declareMock` fails if no `MockProviderRule` or `MockProviderExtension` is registered.
- Overrides stay for the whole test: a definition replaced with a test module, `declareMock` or `declare` is only reset when Koin stops.

## Related

- [Verifying your Configuration](/docs/reference/koin-test/verify): `verify()` and its relationship with compile-time safety.
- [Android Instrumented Testing](/docs/reference/koin-android/instrumented-testing): tests where the `Application` starts Koin.
- [Testing Composables](/docs/reference/koin-compose/compose-testing): Compose UI tests and previews.
- [Ktor Testing](/docs/reference/koin-ktor/ktor-testing): `testApplication` with Koin.
- [Context Isolation](/docs/reference/koin-core/context-isolation): a private Koin container for libraries and parallel tests.
- [JUnit Test Quickstart](/docs/quickstart/junit-test): a full sample project.
