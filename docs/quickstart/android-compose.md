---
title: Android Jetpack Compose
description: Build an Android app with a Jetpack Compose UI and Koin, declare a ViewModel with the Compiler Plugin DSL and inject it with koinViewModel().
---

In this tutorial you build an Android app with a Jetpack Compose UI that greets a user picked from a list. You learn how to declare a ViewModel in a Koin module, inject it in a composable with `koinViewModel()`, and start Koin from your `Application` class. It takes about 10 minutes and needs Android Studio.

This tutorial uses the Compiler Plugin DSL. For an Android app with annotations, see [Android & Annotations](/docs/quickstart/android-annotations). To compare the styles, see [Choosing your style](/docs/intro/choosing-your-style).

## Get the code

The full sample is in [koin-getting-started/android-compose](https://github.com/InsertKoinIO/koin-getting-started/tree/main/android-compose). Clone the repository and open the `android-compose` folder in Android Studio.

## Gradle setup

The sample applies the Koin Compiler Plugin and adds `koin-android` and `koin-androidx-compose`. Versions live in a version catalog:

```toml
# gradle/libs.versions.toml
[versions]
# Kotlin, Android Gradle Plugin, AndroidX and Compose entries omitted
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
koin-androidx-compose = { module = "io.insert-koin:koin-androidx-compose", version.ref = "koin" }
koin-test-junit4 = { module = "io.insert-koin:koin-test-junit4", version.ref = "koin" }
koin-android-test = { module = "io.insert-koin:koin-android-test", version.ref = "koin" }

[plugins]
koin-compiler = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }
```

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.koin.compiler)
}

android {
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Compose BOM, ui, material3, activity-compose, lifecycle-viewmodel-compose
    implementation(platform(libs.compose.bom))
    ...

    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.koin.android.test)
}
```

The Compiler Plugin needs Kotlin 2.3.20 or later. For current versions and the BOM, see [Gradle setup](/docs/setup/gradle). For plugin options, see [Compiler Plugin setup](/docs/setup/compiler-plugin).

## The application

The main screen asks a ViewModel for a greeting. The ViewModel uses `UserService`, which reads from `UserRepository`:

```
User -> UserRepository -> UserService -> UserViewModel -> MainScreen
```

## Let's write the user data

Start with the data class and a default list of users:

```kotlin
data class User(val name: String, val email: String)

val DEFAULT_USER = User("Alice", "alice@example.com")

val DEFAULT_USERS = listOf(
    DEFAULT_USER,
    User("Bob", "bob@example.com"),
    User("Charlie", "charlie@example.com")
)
```

These users are the data that the repository stores.

## Let's write the repository

The repository keeps users in memory and finds one by name:

```kotlin
interface UserRepository {
    fun findUserOrNull(name: String): User?
    fun addUsers(users: List<User>)
}

class UserRepositoryImpl : UserRepository {

    private val _users = arrayListOf<User>()

    override fun findUserOrNull(name: String): User? {
        return _users.firstOrNull { it.name == name }
    }

    override fun addUsers(users: List<User>) {
        _users.addAll(users)
    }
}
```

`UserRepositoryImpl` has no dependency, so Koin can create it with no extra information.

## Let's write the service

`UserService` loads the users when it is created and prepares the greeting:

```kotlin
interface UserService {
    fun getUserOrNull(name: String = DEFAULT_USER.name): User?
    fun loadUsers()
    fun prepareHelloMessage(user: User?): String
}

class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    init {
        loadUsers()
    }

    override fun getUserOrNull(name: String): User? = userRepository.findUserOrNull(name)

    override fun loadUsers() {
        userRepository.addUsers(DEFAULT_USERS)
    }

    override fun prepareHelloMessage(user: User?): String {
        return user?.let { "Hello '${user.name}' (${user.email})!" } ?: "User not found"
    }
}
```

`UserServiceImpl` depends on `UserRepository`. Koin passes it through the constructor.

## Let's write the ViewModel

The ViewModel turns a name into a greeting:

```kotlin
class UserViewModel(
    private val userService: UserService
) : ViewModel() {

    fun sayHello(name: String): String {
        val user = userService.getUserOrNull(name)
        val message = userService.prepareHelloMessage(user)
        return "[UserViewModel] $message"
    }
}
```

It receives `UserService` through its constructor, like the other components.

## Let's declare the Koin module

A module is where you declare the components Koin creates (see [Core Concepts](/docs/intro/core-concepts)). Declare the repository and the service as singletons bound to their interfaces, and the ViewModel with `viewModel`:

```kotlin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
    viewModel<UserViewModel>()
}
```

The Compiler Plugin reads each constructor and checks at build time that every dependency is declared. Definition types are described in [Definitions](/docs/reference/koin-core/definitions).

## Let's inject the ViewModel in a composable

Set the Compose content in a `ComponentActivity`, and get the ViewModel with `koinViewModel()` as a default parameter of `MainScreen`:

```kotlin
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: UserViewModel = koinViewModel()
) {
    var nameInput by remember { mutableStateOf("") }
    var greetingMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Koin Sample") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Enter name") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val userName = nameInput.trim().ifEmpty { "Alice" }
                    greetingMessage = viewModel.sayHello(userName)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Say Hello")
            }

            if (greetingMessage.isNotEmpty()) {
                Text(
                    text = greetingMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
```

`koinViewModel()` gets the ViewModel from Koin and ties it to the nearest `ViewModelStoreOwner`, here the Activity. As a default parameter, it lets you pass another instance in previews and tests. The other Compose APIs, such as `koinInject()`, are in [Compose ViewModel](/docs/reference/koin-compose/compose-viewmodel) and [Koin for Compose](/docs/reference/koin-compose/compose).

## Let's start Koin

Start Koin in your `Application` class, and register that class in `AndroidManifest.xml`:

```kotlin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
```

`androidContext()` makes the Android `Context` available to your definitions. Run the app, type a name and tap the button to see the greeting. See [Starting Koin on Android](/docs/reference/koin-android/start) for the other options.

## Next steps

- [Core Concepts](/docs/intro/core-concepts): the mental model behind modules, definitions and injection.
- [Koin for Compose](/docs/reference/koin-compose/compose): `koinInject()`, `KoinApplication` and Compose setup.
- [Compose Multiplatform](/docs/quickstart/cmp): share the Compose UI and Koin modules with iOS.
- [Compose testing](/docs/reference/koin-compose/compose-testing): test composables that use Koin.
- [Tooling](/docs/tooling): IDE plugin and monitoring tools for Koin.
