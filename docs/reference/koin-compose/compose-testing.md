---
title: Testing Composables
description: Give Compose previews their own Koin instance with KoinApplicationPreview, and run Compose UI tests with test modules, fakes and mocks.
---

This page covers what is specific to Compose when you test with Koin: previews with `KoinApplicationPreview`, UI tests with a `ComposeTestRule`, and multiplatform UI tests. It assumes you know how to inject in Composables with `koinInject()` and `koinViewModel()` (see [Koin for Compose](/docs/reference/koin-compose/compose)). Test setup, overrides and `declareMock` are explained in [Unit Testing](/docs/reference/koin-test/testing).

## Previews with KoinApplicationPreview

Previews do not run your `Application` class, so no Koin container exists. `KoinApplicationPreview` creates one for the preview:

```kotlin
import org.koin.compose.KoinApplicationPreview

val previewModule = module {
    single<UserRepository> { FakeUserRepository(sampleUsers) }
    viewModel { UserViewModel(get()) }
}

@Preview
@Composable
fun UserScreenPreview() {
    KoinApplicationPreview(application = { modules(previewModule) }) {
        UserScreen()
    }
}
```

`KoinApplicationPreview` creates a local container with `koinApplication`, not the global one started by `startKoin`. Each preview gets its own instance, so Android Studio can render several previews in parallel.

A lambda definition like `single<UserRepository> { FakeUserRepository(sampleUsers) }` works in every style. The `application` lambda accepts anything you write in `startKoin { }`, so you can also load production modules and override only what the preview needs.

To preview several states, give each preview a module with a different fake:

```kotlin
@Preview(name = "Error")
@Composable
fun UserScreenErrorPreview() {
    KoinApplicationPreview(application = {
        modules(previewModule, module {
            single<UserRepository> { FailingUserRepository() }
        })
    }) {
        UserScreen()
    }
}
```

Several `@Preview` annotations on the same function (light and dark mode, font scale) share the same module.

## UI tests with ComposeTestRule

`koinInject()` and `koinViewModel()` resolve from the global context when no Koin instance is provided by the composition. Start Koin with `KoinTestRule` and set the content:

```kotlin
class UserScreenTest : KoinTest {

    @get:Rule(order = 0)
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<UserRepository> { FakeUserRepository(sampleUsers) }
            viewModel { UserViewModel(get()) }
        })
    }

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Test
    fun displaysUserList() {
        composeTestRule.setContent {
            UserScreen()
        }

        composeTestRule.onNodeWithText("Alice").assertIsDisplayed()
    }
}
```

If the Composable you test contains `KoinApplication(configuration = ...)`, it attaches to the container started by the rule and ignores its own configuration. Your test modules are used.

`KoinContext { }` is deprecated and not needed: do not wrap test content with it.

On Android, these tests run as instrumented tests (or with Robolectric). If the graph needs a `Context`, add `androidContext(...)` in the rule, as shown in [Android Instrumented Testing](/docs/reference/koin-android/instrumented-testing#restart-koin-with-a-test-rule).

### Fakes and mocks

Use a fake to drive the screen into each state:

```kotlin
class UserScreenStatesTest : KoinTest {

    private val fakeRepository = FakeUserRepository()

    @get:Rule(order = 0)
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<UserRepository> { fakeRepository }
            viewModel { UserViewModel(get()) }
        })
    }

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Test
    fun showsErrorMessage() {
        fakeRepository.shouldFail = true

        composeTestRule.setContent { UserScreen() }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Network error")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}
```

Change the fake before `setContent`: the ViewModel is created on first composition and loads its data from there.

To use a mocking library instead, call `declareMock<UserRepository> { ... }` before `setContent` (see [Mocking with declareMock](/docs/reference/koin-test/testing#mocking-with-declaremock)).

### Screens using an isolated context

If the screen gets its Koin instance from `KoinIsolatedContext` (see [Isolated Context](/docs/reference/koin-compose/isolated-context)), the test doesn't need the global context. Build a test container and provide it the same way:

```kotlin
@Test
fun sdkScreenUsesTestContainer() {
    val testKoin = koinApplication {
        modules(module { single<SdkApi> { FakeSdkApi() } })
    }

    composeTestRule.setContent {
        KoinIsolatedContext(context = testKoin) {
            SdkScreen()
        }
    }

    composeTestRule.onNodeWithText("Connected").assertIsDisplayed()
    testKoin.close()
}
```

No global state is involved, so these tests can run in parallel.

## Multiplatform UI tests

In `commonTest`, JUnit rules are not available. Use `runComposeUiTest` from the Compose Multiplatform test library, and start and stop Koin with `kotlin.test` annotations:

```kotlin
// commonTest
@OptIn(ExperimentalTestApi::class)
class UserScreenCommonTest : KoinTest {

    @BeforeTest
    fun before() {
        startKoin {
            modules(module {
                single<UserRepository> { FakeUserRepository(sampleUsers) }
                viewModel { UserViewModel(get()) }
            })
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun displaysUserList() = runComposeUiTest {
        setContent { UserScreen() }

        onNodeWithText("Alice").assertIsDisplayed()
    }
}
```

`KoinTest`, `startKoin` and `declareMock` are multiplatform. `KoinTestRule` and `MockProviderRule` are JVM only. Set up the test dependencies as described in the [Compose Multiplatform testing guide](https://kotlinlang.org/docs/multiplatform/compose-test.html), and add `io.insert-koin:koin-test` to `commonTest`.

## Pitfalls

- Rule order: Koin must start before the content is set. With `createAndroidComposeRule<MainActivity>()`, the Activity is launched by the rule, so give `KoinTestRule` the lower `order`.
- ViewModel already created: a ViewModel created in an earlier composition keeps its dependencies. Change fakes or mocks before `setContent`.
- Preview containers are local: code that reads the global context (`KoinPlatform.getKoin()`, a `KoinComponent` outside Compose) does not see the `KoinApplicationPreview` container.

## Related

- [Unit Testing](/docs/reference/koin-test/testing): `KoinTestRule`, overrides and `declareMock`.
- [Android Instrumented Testing](/docs/reference/koin-android/instrumented-testing): test Application, rule order with Activities.
- [Koin for Compose](/docs/reference/koin-compose/compose): `koinInject()` and `KoinApplication`.
- [ViewModel in Compose](/docs/reference/koin-compose/compose-viewmodel): how `koinViewModel()` creates and keeps ViewModels.
- [Isolated Context](/docs/reference/koin-compose/isolated-context): `KoinIsolatedContext` for SDKs and libraries.
