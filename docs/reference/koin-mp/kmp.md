---
title: KMP Advanced Patterns
---

# KMP Advanced Patterns

This guide covers advanced patterns for Koin in Kotlin Multiplatform projects.

:::info
For basic setup, see [KMP Setup](/docs/reference/koin-core/kmp-setup). For module organization, see [Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules). For ViewModel, see [ViewModel](/docs/reference/koin-core/viewmodel).
:::

## Source Project

:::info
You can find the Kotlin Multiplatform project here: https://github.com/InsertKoinIO/hello-kmp
:::

## Advanced expect/actual Patterns

Beyond the basic `expect val platformModule: Module` pattern, here are advanced approaches for platform-specific code.

### Pattern 1: expect/actual Classes

Use when you need platform-specific APIs (Android Context, iOS UIDevice, etc.):

```kotlin
// commonMain - Declaration
expect class PlatformContext

expect fun createPlatformModule(): Module

// androidMain - Android Implementation
actual class PlatformContext(val context: Context)

actual fun createPlatformModule() = module {
    single<PlatformContext>()  // Compiler Plugin DSL
}

// iosMain - iOS Implementation
actual class PlatformContext

actual fun createPlatformModule() = module {
    single<PlatformContext>()
}
```

### Pattern 2: Interface + Platform Implementations

Use when you want to inject different implementations per platform:

```kotlin
// commonMain - Interface
interface Logger {
    fun log(message: String)
}

// androidMain
class AndroidLogger : Logger {
    override fun log(message: String) {
        android.util.Log.d("App", message)
    }
}

val androidModule = module {
    single<AndroidLogger>() bind Logger::class
}

// iosMain
class IOSLogger : Logger {
    override fun log(message: String) {
        println("iOS: $message")
    }
}

val iosModule = module {
    single<IOSLogger>() bind Logger::class
}
```

### Pattern 3: expect Module with Annotations

Combine expect/actual with annotations for cleaner code:

```kotlin
// commonMain
expect val platformModule: Module

// androidMain
@Module
@ComponentScan("com.myapp.android")
class AndroidPlatformModule

actual val platformModule = AndroidPlatformModule().module

// iosMain
@Module
@ComponentScan("com.myapp.ios")
class IosPlatformModule

actual val platformModule = IosPlatformModule().module
```

:::info
**When to use which pattern:**
- **expect/actual classes**: Platform APIs (Context, UIDevice), simple platform differences
- **Interfaces**: Business logic that varies by platform, testable code
- **expect modules**: Complex platform-specific dependency graphs
:::

## Android Context in Shared Code

A common need is accessing Android `Context` in shared code. Here's the recommended pattern:

### ContextWrapper Pattern

```kotlin
// commonMain - Wrapper interface
interface AppContext

// androidMain - Android implementation
class AndroidAppContext(val context: Context) : AppContext

val androidContextModule = module {
    single<AndroidAppContext>() bind AppContext::class
}

// iosMain - Empty implementation
class IOSAppContext : AppContext

val iosContextModule = module {
    single<IOSAppContext>() bind AppContext::class
}
```

Usage in shared code:

```kotlin
// commonMain - Repository uses platform context
class FileRepository(private val appContext: AppContext) {
    fun saveFile(data: String) {
        when (appContext) {
            is AndroidAppContext -> {
                val file = File(appContext.context.filesDir, "data.txt")
                file.writeText(data)
            }
            is IOSAppContext -> {
                // iOS-specific file operations
            }
        }
    }
}

val sharedModule = module {
    single<FileRepository>()
}
```

:::note
For pure shared logic, prefer abstracting platform operations into interfaces rather than using `when` statements.
:::

## Architecture Patterns

### Repository Pattern with Ktor

```kotlin
// commonMain
interface UserRepository {
    suspend fun getUser(id: String): User
    suspend fun saveUser(user: User)
}

@Singleton
class UserRepositoryImpl(
    private val api: UserApi,
    private val database: UserDatabase
) : UserRepository {
    override suspend fun getUser(id: String): User {
        return try {
            api.fetchUser(id).also { database.saveUser(it) }
        } catch (e: Exception) {
            database.getUser(id)
        }
    }

    override suspend fun saveUser(user: User) {
        database.saveUser(user)
        api.updateUser(user)
    }
}

val dataModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
}
```

### Network Layer (Ktor + Koin)

```kotlin
// commonMain
@Singleton
class ApiClient(private val client: HttpClient) {
    suspend fun fetchUser(id: String): User {
        return client.get("https://api.example.com/users/$id").body()
    }
}

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
        }
    }
    single<ApiClient>()
}
```

### Database Layer (SqlDelight)

```kotlin
// commonMain
expect class DriverFactory {
    fun createDriver(): SqlDriver
}

val databaseModule = module {
    single { DriverFactory().createDriver() }
    single { AppDatabase(get()) }
    single { get<AppDatabase>().userQueries }
}

// androidMain
actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context, "app.db")
    }
}

// iosMain
actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema, "app.db")
    }
}
```

## Testing KMP Modules

### Unit Testing Shared Modules

```kotlin
// commonTest
class UserRepositoryTest : KoinTest {

    @Test
    fun testGetUser() = runTest {
        startKoin {
            modules(module {
                single<UserApi> { FakeUserApi() }
                single<UserDatabase> { FakeUserDatabase() }
                single<UserRepositoryImpl>() bind UserRepository::class
            })
        }

        val repository: UserRepository = get()
        val user = repository.getUser("123")

        assertEquals("John", user.name)

        stopKoin()
    }
}
```

### Testing with Platform-Specific Dependencies

```kotlin
// commonTest
expect fun createTestPlatformModule(): Module

// androidTest
actual fun createTestPlatformModule() = module {
    single<PlatformContext> { TestAndroidContext() }
}

// iosTest
actual fun createTestPlatformModule() = module {
    single<PlatformContext> { TestIOSContext() }
}

// commonTest - Test using platform module
class PlatformDependentTest : KoinTest {
    @Test
    fun testWithPlatformContext() {
        startKoin {
            modules(
                createTestPlatformModule(),
                module {
                    single<MyService>()
                }
            )
        }

        val service: MyService = get()
        // Test service

        stopKoin()
    }
}
```

## Common Pitfalls

### DO: Use interfaces for testable shared code

```kotlin
// Good - Testable
interface Logger {
    fun log(message: String)
}

val sharedModule = module {
    single<UserService>()  // Depends on Logger interface
}
```

### DON'T: Use expect classes for business logic

```kotlin
// Bad - Hard to test, tight platform coupling
expect class Logger {
    fun log(message: String)
}
```

### DO: Keep platform modules separate

```kotlin
// Good - Clear separation
fun initKoin() {
    startKoin {
        modules(commonModules() + platformModule)
    }
}
```

### DON'T: Mix platform-specific code in shared modules

```kotlin
// Bad - Platform-specific code in commonMain
val sharedModule = module {
    single {
        if (Platform.isAndroid) { /* ... */ } // Don't do this!
    }
}
```

### DO: Use lazy modules for large apps

```kotlin
// Good - Optimize startup
val lazyFeatureModule = lazyModule {
    single<HeavyService>()
}

startKoin {
    modules(coreModules)
    lazyModules(lazyFeatureModule)
}
```

### DON'T: Forget to close scopes

```kotlin
// Bad - Memory leak
class FeatureScreen : KoinComponent {
    val scope = getKoin().createScope<FeatureScreen>()
    // Forgot to close scope!
}

// Good - Proper cleanup
class FeatureScreen : KoinComponent {
    val scope = getKoin().createScope<FeatureScreen>()

    fun onDestroy() {
        scope.close()
    }
}
```

## Desktop Platform Integration

For JVM Desktop apps (Compose Desktop):

```kotlin
// desktopMain
fun main() = application {
    startKoin {
        modules(
            sharedModule,
            desktopModule
        )
    }

    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

val desktopModule = module {
    single<DesktopLogger>() bind Logger::class
    single<DesktopFileManager>()
}
```

## Web Platform Integration (Experimental)

For Kotlin/JS and Kotlin/WASM:

```kotlin
// jsMain or wasmJsMain
fun main() {
    startKoin {
        modules(
            sharedModule,
            webModule
        )
    }
    // Your web app initialization
}

val webModule = module {
    single<ConsoleLogger>() bind Logger::class
    single<BrowserStorage>()
}
```

:::warning
WASM support is experimental. Some features may not work as expected.
:::

## iOS Swift Interop

### KoinComponent for Swift

```kotlin
// shared/src/iosMain/kotlin/Helper.kt
class GreetingHelper : KoinComponent {
    private val greeting: Greeting by inject()
    fun greet(): String = greeting.greeting()
}
```

In Swift:

```swift
struct ContentView: View {
    let greet = GreetingHelper().greet()

    var body: some View {
        Text(greet)
    }
}
```

### Threading Considerations

On iOS and other Native targets, Koin instances work seamlessly with the new memory model:

- Koin definitions are thread-safe
- Scopes can be created and used across threads
- Use `@SharedImmutable` for global Koin instances if needed

:::note
The new Kotlin/Native memory model (default in Kotlin 1.7.20+) makes Koin usage much simpler.
:::

## Next Steps

- **[KMP Setup](/docs/reference/koin-core/kmp-setup)** - Basic KMP configuration
- **[Sharing Patterns](/docs/reference/koin-core/kmp-shared-modules)** - Module organization
- **[ViewModel](/docs/reference/koin-core/viewmodel)** - Multiplatform ViewModel
- **[Koin for Compose](/docs/reference/koin-compose/compose)** - Compose integration
- **[Annotations KMP](/docs/reference/koin-annotations/kmp)** - Annotation-based DI in KMP
