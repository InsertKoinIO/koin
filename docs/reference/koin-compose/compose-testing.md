---
title: Testing Composables
---

# Testing Composables with Koin

This guide covers testing strategies for Compose applications using Koin, from Android Studio previews to comprehensive unit tests.

## KoinApplicationPreview

Use `KoinApplicationPreview` for Android Studio previews with Koin dependencies:

```kotlin
@Preview
@Composable
fun UserScreenPreview() {
    KoinApplicationPreview(application = {
        modules(module {
            viewModel { UserViewModel(FakeUserRepository()) }
        })
    }) {
        UserScreen()
    }
}
```

### Multiple Previews

```kotlin
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large Font", fontScale = 1.5f)
@Composable
fun UserCardPreviews() {
    KoinApplicationPreview(application = {
        modules(previewModule)
    }) {
        UserCard(user = sampleUser)
    }
}

val previewModule = module {
    single<UserRepository> { FakeUserRepository() }
    viewModel { UserViewModel(get()) }
}
```

### Preview with Different States

```kotlin
@Preview(name = "Loading")
@Composable
fun LoadingPreview() {
    KoinApplicationPreview(application = {
        modules(module {
            viewModel { UserViewModel(LoadingRepository()) }
        })
    }) {
        UserScreen()
    }
}

@Preview(name = "Error")
@Composable
fun ErrorPreview() {
    KoinApplicationPreview(application = {
        modules(module {
            viewModel { UserViewModel(ErrorRepository()) }
        })
    }) {
        UserScreen()
    }
}

@Preview(name = "Success")
@Composable
fun SuccessPreview() {
    KoinApplicationPreview(application = {
        modules(module {
            viewModel { UserViewModel(SuccessRepository(sampleUsers)) }
        })
    }) {
        UserScreen()
    }
}
```

## Unit Testing with ComposeTestRule

### Basic Setup

```kotlin
class UserScreenTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<UserRepository> { FakeUserRepository() }
            viewModel { UserViewModel(get()) }
        })
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysUserList() {
        composeTestRule.setContent {
            UserScreen()
        }

        composeTestRule.onNodeWithText("Alice").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bob").assertIsDisplayed()
    }
}
```

### Testing User Interactions

```kotlin
@Test
fun clickingUserShowsDetails() {
    composeTestRule.setContent {
        UserScreen()
    }

    // Click on a user
    composeTestRule.onNodeWithText("Alice").performClick()

    // Verify navigation or state change
    composeTestRule.onNodeWithText("alice@example.com").assertIsDisplayed()
}

@Test
fun searchFiltersUsers() {
    composeTestRule.setContent {
        UserScreen()
    }

    // Enter search query
    composeTestRule.onNodeWithTag("searchField").performTextInput("Ali")

    // Verify filtered results
    composeTestRule.onNodeWithText("Alice").assertIsDisplayed()
    composeTestRule.onNodeWithText("Bob").assertDoesNotExist()
}
```

### Testing with ViewModel States

```kotlin
@Test
fun showsLoadingIndicator() {
    val loadingRepository = object : UserRepository {
        override suspend fun getUsers(): List<User> {
            delay(Long.MAX_VALUE) // Never completes
            return emptyList()
        }
    }

    startKoin {
        modules(module {
            single<UserRepository> { loadingRepository }
            viewModel { UserViewModel(get()) }
        })
    }

    composeTestRule.setContent {
        UserScreen()
    }

    composeTestRule.onNodeWithTag("loadingIndicator").assertIsDisplayed()

    stopKoin()
}

@Test
fun showsErrorMessage() {
    val errorRepository = object : UserRepository {
        override suspend fun getUsers(): List<User> {
            throw IOException("Network error")
        }
    }

    startKoin {
        modules(module {
            single<UserRepository> { errorRepository }
            viewModel { UserViewModel(get()) }
        })
    }

    composeTestRule.setContent {
        UserScreen()
    }

    composeTestRule.waitUntil(5000) {
        composeTestRule
            .onAllNodesWithText("Network error")
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

    stopKoin()
}
```

## Mocking Dependencies

### Using MockK

```kotlin
class UserScreenMockTest : KoinTest {

    private val mockRepository = mockk<UserRepository>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single { mockRepository }
            viewModel { UserViewModel(get()) }
        })
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadsUsersOnStart() = runTest {
        coEvery { mockRepository.getUsers() } returns listOf(
            User("Test User", "test@example.com")
        )

        composeTestRule.setContent {
            UserScreen()
        }

        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodesWithText("Test User")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        coVerify { mockRepository.getUsers() }
    }

    @Test
    fun refreshCallsRepository() = runTest {
        coEvery { mockRepository.getUsers() } returns emptyList()

        composeTestRule.setContent {
            UserScreen()
        }

        // Trigger refresh
        composeTestRule.onNodeWithTag("refreshButton").performClick()

        coVerify(exactly = 2) { mockRepository.getUsers() }
    }
}
```

### Using Fake Implementations

```kotlin
class FakeUserRepository : UserRepository {
    private val users = mutableListOf<User>()
    var shouldFail = false

    override suspend fun getUsers(): List<User> {
        if (shouldFail) throw IOException("Fake error")
        return users.toList()
    }

    override suspend fun addUser(user: User) {
        users.add(user)
    }

    fun setUsers(vararg newUsers: User) {
        users.clear()
        users.addAll(newUsers)
    }
}

class UserScreenFakeTest : KoinTest {

    private val fakeRepository = FakeUserRepository()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<UserRepository> { fakeRepository }
            viewModel { UserViewModel(get()) }
        })
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        fakeRepository.setUsers(
            User("Alice", "alice@example.com"),
            User("Bob", "bob@example.com")
        )
    }

    @Test
    fun displaysUsers() {
        composeTestRule.setContent {
            UserScreen()
        }

        composeTestRule.onNodeWithText("Alice").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bob").assertIsDisplayed()
    }

    @Test
    fun handlesError() {
        fakeRepository.shouldFail = true

        composeTestRule.setContent {
            UserScreen()
        }

        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodesWithText("Error")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}
```

## Testing Navigation

```kotlin
class NavigationTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testModule)
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navigatesToDetail() {
        lateinit var navController: NavHostController

        composeTestRule.setContent {
            navController = rememberNavController()
            AppNavigation(navController)
        }

        // Navigate to detail
        composeTestRule.onNodeWithText("View Details").performClick()

        // Verify navigation
        assertEquals("detail/123", navController.currentDestination?.route)
    }

    @Test
    fun backNavigationWorks() {
        lateinit var navController: NavHostController

        composeTestRule.setContent {
            navController = rememberNavController()
            AppNavigation(navController)
        }

        // Navigate forward
        composeTestRule.onNodeWithText("View Details").performClick()

        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Verify back at home
        assertEquals("home", navController.currentDestination?.route)
    }
}
```

## Testing with Coroutines

```kotlin
class CoroutineTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(module {
            single<UserRepository> { FakeUserRepository() }
            viewModel { UserViewModel(get()) }
        })
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun asyncOperationCompletes() = runTest {
        composeTestRule.setContent {
            UserScreen()
        }

        // Wait for async operation
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("userList")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Verify result
        composeTestRule.onNodeWithText("Alice").assertIsDisplayed()
    }
}
```

## Multiplatform Testing

For Compose Multiplatform, create expect/actual test helpers:

```kotlin
// commonTest
expect fun createTestComposeRule(): ComposeTestRule

// androidTest
actual fun createTestComposeRule(): ComposeTestRule = createComposeRule()

// Common test
class CommonUserScreenTest : KoinTest {

    @get:Rule
    val composeTestRule = createTestComposeRule()

    @Test
    fun displaysContent() {
        startKoin {
            modules(testModule)
        }

        composeTestRule.setContent {
            UserScreen()
        }

        // Assertions...

        stopKoin()
    }
}
```

## Best Practices

1. **Use KoinTestRule** - handles setup/teardown automatically
   ```kotlin
   @get:Rule
   val koinTestRule = KoinTestRule.create { modules(testModule) }
   ```

2. **Prefer Fakes over Mocks** - more predictable, easier to understand

3. **Test one behavior at a time** - focused tests are easier to maintain

4. **Use semantic test tags** - make tests resilient to UI changes
   ```kotlin
   Modifier.testTag("submitButton")
   ```

5. **Wait for async operations** - use `waitUntil` for async state
   ```kotlin
   composeTestRule.waitUntil(5000) { condition }
   ```

6. **Clean up Koin** - if not using rules, call `stopKoin()` in `@After`

## Next Steps

- **[Compose Overview](/docs/reference/koin-compose/compose)** - Setup and basic injection
- **[Testing Reference](/docs/reference/koin-test/testing)** - General Koin testing
- **[Module Verification](/docs/reference/koin-test/verify)** - Verify module configuration
