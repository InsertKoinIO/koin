---
title: Android Instrumented Testing
description: Replace Koin definitions in Android instrumented tests with a test Application or a test rule, and test Activities, Fragments and scopes.
---

Instrumented tests run on a device or emulator, inside your real `Application`. Koin is already started by `Application.onCreate()` when the test begins, so the question is how to replace production definitions with test ones. This guide covers the setups for that, then Activity, Fragment and scope tests. The generic techniques (`KoinTest`, `KoinTestRule`, `declareMock`) are explained in [Unit Testing](/docs/reference/koin-test/testing).

## Dependencies

```kotlin
dependencies {
    androidTestImplementation("io.insert-koin:koin-test-junit4")
}
```

`koin-test-junit4` brings `koin-test`. Versions come from the Koin BOM, see [Gradle Setup](/docs/setup/gradle).

## Choose a setup

| Setup | Koin is started by | Use it when |
|-------|--------------------|-------------|
| [Test Application](#use-a-test-application) | A test `Application` class | All tests share the same test configuration |
| [Test rule](#restart-koin-with-a-test-rule) | `KoinTestRule`, before each test | Each test class needs its own modules |
| [Load overrides](#load-overrides-into-the-running-container) | Your production `Application` | You only replace a few definitions |

## Use a test Application

Create an `Application` in `androidTest` that starts Koin with your production modules plus a test module. The test module is loaded last, so its definitions replace the production ones:

<Tabs groupId="koin-style">
<TabItem value="compiler-dsl" label="Compiler Plugin DSL" default>

```kotlin
// in src/androidTest
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TestApplication)
            modules(appModule, instrumentedTestModule)
        }
    }
}
```

</TabItem>
<TabItem value="annotations" label="Annotations">

```kotlin
// in src/androidTest
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin<MyApp> {
            androidContext(this@TestApplication)
            modules(instrumentedTestModule)
        }
    }
}
```

`startKoin<MyApp>` loads the modules of your `@KoinApplication` class first, then the modules declared in the lambda.

</TabItem>
<TabItem value="classic-dsl" label="Constructor / Classic DSL">

```kotlin
// in src/androidTest
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TestApplication)
            modules(appModule, instrumentedTestModule)
        }
    }
}
```

</TabItem>
</Tabs>

```kotlin
val instrumentedTestModule = module {
    single<ApiService> { FakeApiService() }
}
```

A lambda definition like `single<ApiService> { FakeApiService() }` works in every style.

To run tests with this `Application`, create a custom [AndroidJUnitRunner](https://developer.android.com/training/testing/instrumented-tests/androidx-test-libraries/runner):

```kotlin
class InstrumentationTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(classLoader, TestApplication::class.java.name, context)
    }
}
```

Register it in `build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        testInstrumentationRunner = "com.example.app.InstrumentationTestRunner"
    }
}
```

## Restart Koin with a test rule

`KoinTestRule` closes the running Koin container, starts a new one before each test and stops it after. Pass the Android context yourself, since your `Application` is not the one starting this container:

```kotlin
import androidx.test.core.app.ApplicationProvider
import org.koin.android.ext.koin.androidContext
import org.koin.test.KoinTestRule

@RunWith(AndroidJUnit4::class)
class LoginActivityTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(ApplicationProvider.getApplicationContext())
        modules(appModule, loginTestModule)
    }
}
```

With annotations, use `withConfiguration<MyApp>()` in place of `modules(appModule)`, as in [Unit Testing](/docs/reference/koin-test/testing#junit-4-kointestrule).

The production `Application` still runs `startKoin` once when the process starts. The rule replaces that container, so anything the `Application` resolved in `onCreate()` holds instances from the closed one. If your `Application` resolves dependencies at startup, prefer a [test Application](#use-a-test-application) that does not.

## Load overrides into the running container

To keep the production `Application` and change only a few definitions, load a test module into the running container before the screen under test starts:

```kotlin
@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest : KoinTest {

    private val profileTestModule = module {
        single<UserRepository> { FakeUserRepository() }
    }

    @Before
    fun before() {
        loadKoinModules(profileTestModule)
    }
}
```

This is the lightest setup, and the one with the most pitfalls:

- No reset: the override stays for every following test in the process.
- Unloading removes, it does not restore: `unloadKoinModules(profileTestModule)` deletes the `UserRepository` definition. The production one does not come back.
- Created instances are kept: a `single` that already received the production `UserRepository` keeps it.

If you need a clean graph per test, use the [test rule](#restart-koin-with-a-test-rule) instead.

## Mocks and fakes

`declareMock` works in instrumented tests as in unit tests: implement `KoinTest` and register a `MockProviderRule` (see [Mocking with declareMock](/docs/reference/koin-test/testing#mocking-with-declaremock)). With MockK, use the `io.mockk:mockk-android` artifact on the device.

Fakes are often simpler on a device: they need no mocking library and behave like the real class. Declare them in the test module. For Room, use an in-memory database so each test starts empty:

```kotlin
val testDatabaseModule = module {
    single {
        Room.inMemoryDatabaseBuilder(androidContext(), AppDatabase::class.java).build()
    }
    single { get<AppDatabase>().userDao() }
}
```

## Activity and Fragment tests

Activities and Fragments resolve their dependencies with `by inject()` and `by viewModel()` (see [Entry Points](/docs/reference/koin-android/entry-points)). In a test, they receive whatever the test container holds, so launch them after Koin is configured:

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(ApplicationProvider.getApplicationContext())
        modules(appModule, module {
            single<AuthService> { FakeAuthService(acceptedEmail = "user@example.com") }
        })
    }

    @Test
    fun successfulLoginShowsWelcomeMessage() {
        ActivityScenario.launch(LoginActivity::class.java).use {
            onView(withId(R.id.email)).perform(typeText("user@example.com"))
            onView(withId(R.id.loginButton)).perform(click())

            onView(withId(R.id.successMessage)).check(matches(isDisplayed()))
        }
    }
}
```

The same works for a Fragment with `launchFragmentInContainer<ProfileFragment>()` from `androidx.fragment:fragment-testing`. The `LoginViewModel` that `LoginActivity` gets with `by viewModel()` is created by Koin with the `FakeAuthService`.

To read a ViewModel or a dependency from the running component, use `scenario.onActivity { activity -> ... }` and access it there. Outside of that block, the Activity may not exist yet.

## Scopes

An [Android scope](/docs/reference/koin-android/scope) exists only while its Activity or Fragment is alive. Test it through the component:

```kotlin
val checkoutTestModule = module {
    activityScope {
        scoped { CheckoutState() }
    }
}

@Test
fun fragmentSharesTheActivityScope() {
    ActivityScenario.launch(CheckoutActivity::class.java).use { scenario ->
        scenario.onActivity { activity ->
            val activityState = activity.scope.get<CheckoutState>()

            activity.supportFragmentManager.commitNow {
                replace(R.id.container, PaymentFragment())
            }
            val fragment = activity.supportFragmentManager
                .findFragmentById(R.id.container) as PaymentFragment

            assertSame(activityState, fragment.scope.get<CheckoutState>())
        }
    }
}
```

`activity.scope` and `fragment.scope` are available when the components implement `AndroidScopeComponent` (for example `ScopeActivity` and `ScopeFragment`). Calling `scope.get()` before the Activity is created, or after it is destroyed, fails: the scope does not exist yet, or is closed.

For scopes that are not tied to an Android component, a JVM unit test is enough: see [Unit Testing](/docs/reference/koin-test/testing#test-scoped-definitions).

## Multi-module apps

In a [multi-module app](/docs/reference/koin-android/multi-module), load the feature modules under test with the core modules they need, then your test overrides last:

```kotlin
@get:Rule
val koinTestRule = KoinTestRule.create {
    androidContext(ApplicationProvider.getApplicationContext())
    modules(
        networkModule,
        databaseModule,
        loginModule,
        homeModule,
        module { single<ApiService> { FakeApiService() } }
    )
}
```

A shared `androidTest` source set or a `:core:testing` Gradle module is a good place for fakes and test modules used by several features.

If you load only part of the app, a definition may miss a dependency that the full app provides. The [Compiler Plugin](/docs/reference/koin-compiler/compile-safety) checks the production graph at build time, not your test modules. To check a test configuration, use [verify()](/docs/reference/koin-test/verify) in a JVM test.

## Espresso and rule order

`ActivityScenarioRule` launches the Activity before the test method runs. If it runs before `KoinTestRule`, the Activity resolves its dependencies from the wrong container. Set the order explicitly:

```kotlin
@RunWith(AndroidJUnit4::class)
@LargeTest
class CheckoutFlowTest {

    @get:Rule(order = 0)
    val koinTestRule = KoinTestRule.create {
        androidContext(ApplicationProvider.getApplicationContext())
        modules(appModule, module {
            single<CartRepository> { FakeCartRepository() }
            single<PaymentService> { FakePaymentService() }
        })
    }

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun completeCheckout() {
        onView(withId(R.id.cartButton)).perform(click())
        onView(withId(R.id.checkoutButton)).perform(click())
        onView(withId(R.id.completeOrderButton)).perform(click())

        onView(withId(R.id.confirmationMessage)).check(matches(isDisplayed()))
    }
}
```

`@get:Rule(order = ...)` needs JUnit 4.13 or later. With older versions, use `RuleChain.outerRule(koinTestRule).around(activityRule)`.

## Compose UI tests

For Compose screens, see [Testing Composables](/docs/reference/koin-compose/compose-testing).

## Isolation between tests

Tests that pass alone but fail together usually share state through Koin:

- Restart per test: `KoinTestRule` gives each test a new container, with new `single` instances.
- Reset fakes: with a [test Application](#use-a-test-application), the container lives for the whole run. A fake declared as `single` keeps its data between tests, so give it a `reset()` method and call it in `@Before`.
- Don't stack overrides: modules loaded with `loadKoinModules` stay loaded (see [Load overrides into the running container](#load-overrides-into-the-running-container)).

```kotlin
@RunWith(AndroidJUnit4::class)
class UserListTest : KoinTest {

    private val fakeRepository: FakeUserRepository by inject()

    @Before
    fun before() {
        fakeRepository.reset()
    }
}
```

`by inject<FakeUserRepository>()` only works if the test module binds the fake to its own type too: `single { FakeUserRepository() } bind UserRepository::class`.

## Troubleshooting

### KoinApplicationAlreadyStartedException

A second `startKoin` call ran while a container was running. In instrumented tests, your `Application` has already started Koin. Use `KoinTestRule`, which closes the running container first, or a [test Application](#use-a-test-application). Do not call `startKoin` in `@Before`.

### The test definition is not used

- Load order: the test module must be loaded after the production module that declares the same type.
- Overrides disabled: if Koin starts with `allowOverride(false)`, mark test definitions with `.override()` (see [Definitions](/docs/reference/koin-core/definitions#overriding-a-definition)).
- Instance already created: a `single` created before the override keeps the old dependency. Restart Koin with the test rule.
- Rule order: the Activity was launched before Koin was configured (see [Espresso and rule order](#espresso-and-rule-order)).

### NoDefinitionFoundException for a scoped definition

The scope was not open when you resolved it. Resolve scoped definitions inside `scenario.onActivity { }` while the Activity is alive, and check that the definition is declared in the right scope (`activityScope`, `fragmentScope`).

## Related

- [Unit Testing](/docs/reference/koin-test/testing): `KoinTest`, `KoinTestRule`, `declareMock` and overrides.
- [Testing Composables](/docs/reference/koin-compose/compose-testing): Compose UI tests and previews.
- [Verifying your Configuration](/docs/reference/koin-test/verify): `verify()` and `androidVerify()`.
- [Android Scopes](/docs/reference/koin-android/scope): Activity, Fragment and ViewModel scopes.
- [Starting Koin on Android](/docs/reference/koin-android/start): `startKoin` in your `Application`.
