---
title: Android Instrumented Testing
---

## Overview

Instrumented tests run on Android devices or emulators and test your app's integration with the Android framework. Unlike unit tests where you control Koin's lifecycle, instrumented tests require special handling because Koin is started by your `Application` class.

### Key Differences from Unit Tests

| Aspect | Unit Tests | Instrumented Tests |
|--------|------------|-------------------|
| **Execution** | JVM only | Android device/emulator |
| **Koin Start** | In test class (`startKoin`) | In `Application.onCreate()` |
| **Speed** | Fast | Slower |
| **Android APIs** | Mocked | Real |
| **Test Isolation** | Easy (each test starts fresh) | Requires careful setup |
| **Use Case** | Business logic, ViewModels | UI, Android components integration |

### What to Test with Instrumented Tests

✅ **Good for instrumented tests:**
- UI behavior and interactions
- Android component integration (Activity, Fragment, Service)
- Navigation flows
- Database operations with Room
- Shared preferences and file I/O
- Compose UI testing

❌ **Better as unit tests:**
- Business logic
- ViewModels (can be unit tested)
- Repositories (can be unit tested with mocks)
- Pure Kotlin functions

## Test Strategies

### Strategy 1: Custom Test Application

Create a separate Application class for tests with test-specific modules.

### Strategy 2: Test Rules

Use JUnit rules to configure Koin per test class or test method.

### Strategy 3: Module Override

Keep production Application but override specific definitions for testing.

Let's explore each strategy in detail.

## Override production modules in a custom Application class

Unlike [unit tests](/docs/reference/koin-test/testing), where you effectively call start Koin in each test class (i.e. `startKoin` or `KoinTestExtension`), in Instrumented tests Koin is started by your `Application` class. 

For overriding production Koin modules, `loadModules` and `unloadModules` are often unsafe because the changes are not applied immediately. Instead, the recommended approach is to add a `module` of your overrides to `modules` used by `startKoin` in the `Application` class.
If you want to keep the class that extends `Application` of your application untouched, you can create another one inside the `AndroidTest` package like:
```kotlin
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(productionModule, instrumentedTestModule)
        }
    }
}
```
In order to use this custom `Application` in yours Instrumentation tests you may need to create a custom [AndroidJUnitRunner](https://developer.android.com/training/testing/instrumented-tests/androidx-test-libraries/runner) like:
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
And then register it inside your gradle file with:
```groovy
testInstrumentationRunner "com.example.myapplication.InstrumentationTestRunner"
```

## Override production modules with a test rule

If you want more flexibility, you still have to create the custom `AndroidJUnitRunner` but instead of having `startKoin { ... }` inside the custom application, you can put it inside a custom test rule like:
```kotlin
class KoinTestRule(
    private val modules: List<Module>
) : TestWatcher() {
    override fun starting(description: Description) {

        if (getKoinApplicationOrNull() == null) {
            startKoin {
                androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
                modules(modules)
            }
        } else {
            loadKoinModules(modules)
        }
    }

    override fun finished(description: Description) {
        unloadKoinModules(modules)
    }
}
```
In this way we can potentially override the definitions directly from our test classes, like:
```kotlin
private val instrumentedTestModule = module {
    factory<Something> { FakeSomething() }
}

@get:Rule
val koinTestRule = KoinTestRule(
    modules = listOf(productionModule, instrumentedTestModule)
)
```

## Mocking and Fakes

### Using `declareMock()` (Recommended)

:::info
**Koin 4.2+:** Use `declareMock()` to quickly mock dependencies on-the-fly in tests without creating separate test modules.
:::

```kotlin
class UserViewModelTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            module {
                viewModelOf(::UserViewModel)
                // Other production dependencies
            }
        )
    }

    @Test
    fun `test user loading`() {
        // Declare mock on the fly
        declareMock<UserRepository> {
            coEvery { getUser(any()) } returns User("1", "Test User")
        }

        val viewModel: UserViewModel by inject()
        // Test with mocked repository
    }
}
```

**Benefits of `declareMock()`:**
- ✅ No need to create separate test modules
- ✅ Mock only what you need per test
- ✅ Cleaner test code
- ✅ Works with MockK out of the box

### Using Test Doubles

Replace real implementations with mocks or fakes for testing:

```kotlin
// Production module
val productionModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single { ApiService.create() }
}

// Test module with fakes
val testModule = module {
    single<UserRepository> { FakeUserRepository() }
    single<ApiService> { FakeApiService() }
}

// Fake implementation
class FakeUserRepository : UserRepository {
    private val users = mutableListOf<User>()

    override suspend fun getUser(id: String): User {
        return users.find { it.id == id } ?: throw UserNotFoundException()
    }

    override suspend fun saveUser(user: User) {
        users.add(user)
    }

    // Test-specific methods
    fun clearUsers() {
        users.clear()
    }
}
```

### Using MockK

```kotlin
// Test module with MockK
val mockModule = module {
    single<UserRepository> {
        mockk<UserRepository> {
            coEvery { getUser(any()) } returns User("1", "Test User")
            coEvery { saveUser(any()) } just Runs
        }
    }
}

// Test application
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TestApplication)
            modules(mockModule)
        }
    }
}
```

### Partial Mocking

Replace only specific dependencies:

```kotlin
val testModule = module {
    // Keep real implementations
    single { Database.create(androidContext()) }

    // Mock network layer
    single<ApiService> { mockk<ApiService>() }

    // Use real repository with mocked API
    single<UserRepository> { UserRepositoryImpl(get()) }
}
```

## Testing Activities and Fragments

### Testing Activity with Koin

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            module {
                viewModel { LoginViewModel(get()) }
                single<AuthService> { FakeAuthService() }
            }
        )
    )

    @Test
    fun testSuccessfulLogin() {
        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.email)).perform(typeText("user@example.com"))
        onView(withId(R.id.password)).perform(typeText("password123"))
        onView(withId(R.id.loginButton)).perform(click())

        onView(withId(R.id.successMessage)).check(matches(isDisplayed()))

        scenario.close()
    }
}
```

### Testing Fragment with Koin

```kotlin
@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            module {
                viewModel { ProfileViewModel(get()) }
                single<UserRepository> {
                    mockk {
                        coEvery { getUser(any()) } returns User("1", "Test User")
                    }
                }
            }
        )
    )

    @Test
    fun testProfileDisplaysUserInfo() {
        val scenario = launchFragmentInContainer<ProfileFragment>()

        onView(withId(R.id.userName)).check(matches(withText("Test User")))

        scenario.close()
    }
}
```

## Testing ViewModels in Instrumented Tests

### Injecting ViewModel in Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            module {
                viewModelOf(::HomeViewModel)
                single<UserRepository> { FakeUserRepository() }
            }
        )
    )

    private val viewModel: HomeViewModel by inject()

    @Test
    fun testLoadUserData() = runTest {
        viewModel.loadUser("123")

        val state = viewModel.userState.value
        assertEquals("Test User", state.name)
    }
}
```

### Testing ViewModel with Activity

```kotlin
@Test
fun testViewModelStateReflectsInUI() {
    val scenario = ActivityScenario.launch(HomeActivity::class.java)

    scenario.onActivity { activity ->
        val viewModel: HomeViewModel = activity.viewModel

        // Trigger ViewModel action
        viewModel.loadUser("123")

        // Verify UI updated
        onView(withId(R.id.userName)).check(matches(withText("Test User")))
    }
}
```

## Testing with Jetpack Compose

### Compose UI Test with Koin

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            module {
                viewModelOf(::LoginViewModel)
                single<AuthService> { FakeAuthService() }
            }
        )
    )

    @Test
    fun testLoginFlow() {
        composeTestRule.setContent {
            KoinContext {
                LoginScreen()
            }
        }

        composeTestRule.onNodeWithTag("email_field")
            .performTextInput("user@example.com")

        composeTestRule.onNodeWithTag("password_field")
            .performTextInput("password123")

        composeTestRule.onNodeWithTag("login_button")
            .performClick()

        composeTestRule.onNodeWithTag("success_message")
            .assertIsDisplayed()
    }
}
```

### Testing Composables with koinViewModel

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val user by viewModel.user.collectAsState()

    Text(text = user?.name ?: "Loading...")
}

// Test
@Test
fun testHomeScreenDisplaysUser() {
    composeTestRule.setContent {
        KoinContext {
            HomeScreen()
        }
    }

    composeTestRule.onNodeWithText("Test User")
        .assertIsDisplayed()
}
```

## Testing Scopes

### Testing Activity Scope

```kotlin
@RunWith(AndroidJUnit4::class)
class CheckoutActivityTest {

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            module {
                activityScope {
                    scoped { CheckoutState() }
                }
            }
        )
    )

    @Test
    fun testActivityScopeSharedAcrossFragments() {
        val scenario = ActivityScenario.launch(CheckoutActivity::class.java)

        scenario.onActivity { activity ->
            val state1 = activity.scope.get<CheckoutState>()
            state1.selectedAddress = Address("123 Main St")

            // Navigate to next fragment
            activity.supportFragmentManager.commit {
                replace(R.id.container, PaymentFragment())
            }

            // Same scope accessible in fragment
            val fragment = activity.supportFragmentManager
                .findFragmentById(R.id.container) as PaymentFragment

            val state2 = fragment.scope.get<CheckoutState>()
            assertEquals(state1, state2)
            assertEquals("123 Main St", state2.selectedAddress?.street)
        }
    }
}
```

### Testing Custom Scopes

```kotlin
@Test
fun testCustomScopeLifecycle() {
    val testModule = module {
        scope(named("session")) {
            scoped { UserSession() }
        }
    }

    koinApplication {
        modules(testModule)

        // Create scope
        val sessionScope = koin.createScope("test_session", named("session"))
        val session = sessionScope.get<UserSession>()

        session.login("user@example.com")
        assertTrue(session.isLoggedIn)

        // Close scope
        sessionScope.close()

        // Scope is closed, can't access
        assertThrows<ClosedScopeException> {
            sessionScope.get<UserSession>()
        }
    }
}
```

## Testing Multi-Module Apps

### Testing with Feature Modules

```kotlin
@RunWith(AndroidJUnit4::class)
class MultiModuleTest {

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            // Core modules
            networkModule,
            databaseModule,

            // Feature modules
            loginModule,
            homeModule,

            // Test overrides
            module {
                single<ApiService>(override = true) { FakeApiService() }
            }
        )
    )

    @Test
    fun testFeatureIntegration() {
        // Test that login feature works with home feature
        val loginViewModel: LoginViewModel by inject()
        val homeViewModel: HomeViewModel by inject()

        runBlocking {
            loginViewModel.login("user@example.com", "password")
            homeViewModel.loadUserData()
        }

        assertEquals("user@example.com", homeViewModel.userState.value.email)
    }
}
```

### Module Verification in Tests

```kotlin
class ModuleVerificationTest {

    @Test
    fun verifyAllModules() {
        // Verify all definitions are satisfied
        appModule.verify()  // appModule includes other modules
    }

    @Test
    fun verifyTestModules() {
        testAppModule.verify()
    }
}
```

:::info
Both `verify()` and `checkModules()` will be replaced by native compile-time safety in the Koin Compiler Plugin. See [Module Verification](/docs/reference/koin-test/verify) for details.
:::

## UI Tests with Espresso

### Complete UI Flow Test

```kotlin
@RunWith(AndroidJUnit4::class)
@LargeTest
class CheckoutFlowTest {

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            module {
                viewModel { CheckoutViewModel(get(), get()) }
                single<CartRepository> { FakeCartRepository() }
                single<PaymentService> { FakePaymentService() }
            }
        )
    )

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testCompleteCheckoutFlow() {
        // Navigate to cart
        onView(withId(R.id.cartButton)).perform(click())

        // Add items to cart
        onView(withId(R.id.addItemButton)).perform(click())
        onView(withId(R.id.cartItemCount)).check(matches(withText("1")))

        // Proceed to checkout
        onView(withId(R.id.checkoutButton)).perform(click())

        // Fill shipping address
        onView(withId(R.id.addressField))
            .perform(typeText("123 Main St"))

        onView(withId(R.id.nextButton)).perform(click())

        // Enter payment info
        onView(withId(R.id.cardNumberField))
            .perform(typeText("4111111111111111"))

        onView(withId(R.id.completeOrderButton)).perform(click())

        // Verify order confirmation
        onView(withId(R.id.confirmationMessage))
            .check(matches(isDisplayed()))
    }
}
```

### Testing Navigation

```kotlin
@Test
fun testNavigationWithSharedState() {
    onView(withId(R.id.loginButton)).perform(click())

    // Login screen
    onView(withId(R.id.emailField)).perform(typeText("user@example.com"))
    onView(withId(R.id.passwordField)).perform(typeText("password"))
    onView(withId(R.id.submitButton)).perform(click())

    // Should navigate to home
    onView(withId(R.id.homeTitle)).check(matches(isDisplayed()))

    // User data should be available (shared through Koin)
    onView(withId(R.id.welcomeMessage))
        .check(matches(withText("Welcome, user@example.com")))
}
```

## Test Isolation

### Ensuring Clean State Between Tests

```kotlin
class KoinIsolationTestRule : TestWatcher() {

    override fun starting(description: Description) {
        // Start fresh Koin instance
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext)
            modules(emptyList())
        }
    }

    override fun finished(description: Description) {
        // Clean up after each test
        stopKoin()
    }
}

@RunWith(AndroidJUnit4::class)
class IsolatedTest {

    @get:Rule
    val isolationRule = KoinIsolationTestRule()

    @Test
    fun test1() {
        loadKoinModules(module { single { "Test1" } })
        assertEquals("Test1", get<String>())
    }

    @Test
    fun test2() {
        // Fresh Koin instance, no pollution from test1
        loadKoinModules(module { single { "Test2" } })
        assertEquals("Test2", get<String>())
    }
}
```

### Resetting Fakes Between Tests

```kotlin
class FakeUserRepository : UserRepository {
    private val users = mutableListOf<User>()

    override suspend fun getUser(id: String): User =
        users.find { it.id == id } ?: throw UserNotFoundException()

    fun reset() {
        users.clear()
    }
}

@RunWith(AndroidJUnit4::class)
class UserTest {

    private val fakeRepo = FakeUserRepository()

    @get:Rule
    val koinTestRule = KoinTestRule(
        modules = listOf(
            module {
                single<UserRepository> { fakeRepo }
            }
        )
    )

    @Before
    fun setup() {
        fakeRepo.reset()
    }

    @Test
    fun test1() {
        // Test with clean repository
    }

    @Test
    fun test2() {
        // Test with clean repository (reset was called)
    }
}
```

## Common Patterns

### Pattern 1: Shared Test Module

```kotlin
// TestModules.kt in androidTest package
object TestModules {

    val fakeNetworkModule = module {
        single<ApiService> { FakeApiService() }
        single { OkHttpClient() }
    }

    val fakeDatabaseModule = module {
        single { createInMemoryDatabase() }
        single<UserDao> { get<AppDatabase>().userDao() }
    }

    val fakeDataModule = module {
        single<UserRepository> { FakeUserRepository() }
    }

    fun createInMemoryDatabase(): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).build()
    }
}

// Use in tests
@get:Rule
val koinTestRule = KoinTestRule(
    modules = TestModules.fakeNetworkModule + TestModules.fakeDataModule
)
```

### Pattern 2: Test-Specific Configuration

```kotlin
class TestConfig {
    companion object {
        const val TEST_API_URL = "http://localhost:8080"
        const val TEST_TIMEOUT_MS = 1000L
    }
}

val testConfigModule = module {
    single {
        OkHttpClient.Builder()
            .connectTimeout(TestConfig.TEST_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(TestConfig.TEST_API_URL)
            .client(get())
            .build()
    }
}
```

### Pattern 3: Per-Test Override

```kotlin
@RunWith(AndroidJUnit4::class)
class FlexibleTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule(modules = emptyList())

    @Test
    fun testWithFakeRepo() {
        loadKoinModules(module {
            single<UserRepository> { FakeUserRepository() }
        })

        // Test code
    }

    @Test
    fun testWithMockRepo() {
        loadKoinModules(module {
            single<UserRepository> { mockk<UserRepository>() }
        })

        // Test code
    }

    @After
    fun cleanup() {
        unloadKoinModules(/* modules loaded in test */)
    }
}
```

## Troubleshooting

### Issue: Koin Already Started

**Problem:**
```
org.koin.core.error.KoinAppAlreadyStartedException: A Koin Application has already been started
```

**Solution:**
```kotlin
class SafeKoinTestRule : TestWatcher() {
    override fun starting(description: Description) {
        // Check if Koin is already started
        if (getKoinApplicationOrNull() == null) {
            startKoin {
                modules(testModules)
            }
        } else {
            // Load modules into existing Koin instance
            loadKoinModules(testModules)
        }
    }

    override fun finished(description: Description) {
        // Don't stop Koin, just unload test modules
        unloadKoinModules(testModules)
    }
}
```

### Issue: Definition Override Doesn't Work

**Problem:**
Test definition doesn't replace production definition.

**Solution:**
```kotlin
// Use override = true
val testModule = module {
    single<UserRepository>(override = true) { FakeUserRepository() }
}

// Or use includes to replace
val testModule = module {
    includes(productionModule)
} + module {
    single<UserRepository>(override = true) { FakeUserRepository() }
}
```

### Issue: Scope Not Found

**Problem:**
```
org.koin.core.error.NoBeanDefFoundException: No definition found for class X
```

**Solution:**
```kotlin
// Ensure scope is created before accessing
val scenario = ActivityScenario.launch(MyActivity::class.java)

scenario.onActivity { activity ->
    // Scope exists here
    val dependency = activity.scope.get<MyDependency>()
}
```

### Issue: Tests Affecting Each Other

**Problem:**
Tests pass individually but fail when run together.

**Solution:**
```kotlin
// Proper cleanup between tests
@After
fun tearDown() {
    // Close scopes
    getKoin().scopeRegistry.deleteScope("test_scope")

    // Reset fakes
    fakeRepository.reset()

    // Unload test modules
    unloadKoinModules(testModules)
}
```

### Issue: ViewModel Not Updating UI

**Problem:**
ViewModel state changes but UI doesn't update in tests.

**Solution:**
```kotlin
// Use Espresso's IdlingResource for async operations
@get:Rule
val activityRule = ActivityScenarioRule(MyActivity::class.java)

@Test
fun testViewModelUpdatesUI() = runTest {
    activityRule.scenario.onActivity { activity ->
        val viewModel: MyViewModel = activity.viewModel

        // Trigger async action
        viewModel.loadData()

        // Wait for LiveData/StateFlow to emit
        advanceUntilIdle()

        // Then verify UI
        onView(withId(R.id.dataText))
            .check(matches(withText("Data Loaded")))
    }
}
```

## Best Practices

### 1. Use In-Memory Database for Tests

```kotlin
val testDatabaseModule = module {
    single {
        Room.inMemoryDatabaseBuilder(
            androidContext(),
            AppDatabase::class.java
        ).build()
    }
}
```

### 2. Keep Test Modules Focused

```kotlin
// ✅ Good - Focused test module
val loginTestModule = module {
    viewModel { LoginViewModel(get()) }
    single<AuthService> { FakeAuthService() }
}

// ❌ Bad - Too broad
val hugeTestModule = module {
    // 50+ definitions...
}
```

### 3. Share Common Fakes

```kotlin
// Create reusable test doubles
object TestDoubles {
    fun createFakeUserRepository() = FakeUserRepository().apply {
        addUser(User("1", "Test User"))
    }

    fun createMockApiService() = mockk<ApiService> {
        coEvery { getUser(any()) } returns User("1", "Test User")
    }
}
```

### 4. Test Real Integration Points

```kotlin
// Test real Room + Repository integration
@Test
fun testDatabaseIntegration() = runTest {
    val database = Room.inMemoryDatabaseBuilder(
        context,
        AppDatabase::class.java
    ).build()

    val repo = UserRepositoryImpl(database.userDao())

    repo.saveUser(User("1", "Test"))
    val user = repo.getUser("1")

    assertEquals("Test", user.name)
}
```

### 5. Use Descriptive Test Names

```kotlin
// ✅ Good
@Test
fun loginWithValidCredentials_navigatesToHomeScreen()

@Test
fun loginWithInvalidEmail_showsEmailError()

// ❌ Bad
@Test
fun test1()

@Test
fun testLogin()
```

## Summary

Key points for instrumented testing with Koin:

- **Custom Test Application** or **Test Rules** for Koin configuration
- **Override modules** using `override = true` or test-specific modules
- **Use fakes over mocks** for better performance in instrumented tests
- **Test isolation** is critical - clean up between tests
- **In-memory databases** for fast, isolated database tests
- **Compose Testing** works seamlessly with `KoinContext`
- **Scope testing** validates lifecycle-bound dependencies
- **Module verification** with `verify()` catches configuration errors early

## Next Steps

- **[Unit Testing](/docs/reference/koin-test/testing)** - Testing strategies for unit tests
- **[Module Verification](/docs/reference/koin-test/verify)** - Verify module configuration
- **[Multi-Module Apps](/docs/reference/koin-android/multi-module)** - Testing multi-module architecture
- **[Best Practices](/docs/reference/koin-android/best-practices)** - Overall Koin best practices
