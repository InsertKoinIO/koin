---
title: Android - Jetpack Compose
---

> This tutorial lets you write an Android application with Jetpack Compose UI and use Koin dependency injection to retrieve your components.
> You need around __10 min__ to do the tutorial.

:::note
update - 2024-11-28
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/android-compose)
:::

## Gradle Setup

Add the Koin Android and Koin Compose dependencies like below:

```groovy
dependencies {

    // Koin for Android
    implementation("io.insert-koin:koin-android:$koin_version")
    // Koin for Jetpack Compose
    implementation("io.insert-koin:koin-androidx-compose:$koin_version")
}
```

## Application Overview

The idea of the application is to manage a list of users, and display it in our `MainActivity` class with a ViewModel and Jetpack Compose UI:

> Users -> UserRepository -> UserService -> UserViewModel -> MainActivity (Compose UI)

## The "User" Data

We will manage a collection of Users. Here is the data class:

```kotlin
data class User(val name: String, val email: String)
```

We create a "Repository" component to manage the list of users (add users or find one by name). Here below, the `UserRepository` interface and its implementation:

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

## The UserService Component

Let's write a service component to manage user operations:

```kotlin
interface UserService {
    fun getUserOrNull(name: String): User?
    fun loadUsers()
    fun prepareHelloMessage(user: User?): String
}

class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun getUserOrNull(name: String): User? = userRepository.findUserOrNull(name)

    override fun loadUsers() {
        userRepository.addUsers(listOf(
            User("Alice", "alice@example.com"),
            User("Bob", "bob@example.com"),
            User("Charlie", "charlie@example.com")
        ))
    }

    override fun prepareHelloMessage(user: User?): String {
        return user?.let { "Hello '${user.name}' (${user.email})! 👋" } ?: "❌ User not found"
    }
}
```

## The Koin module

Use the `module` function to declare a Koin module. A Koin module is the place where we define all our components to be injected.

```kotlin
val appModule = module {

}
```

Let's declare our components. We want singletons of `UserRepository` and `UserService`:

```kotlin
val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

:::info
This tutorial uses the **Koin Compiler Plugin DSL** (`single<T>()`, `viewModel<T>()`) which provides auto-wiring at compile time. See [Compiler Plugin Setup](/docs/setup/compiler-plugin) for configuration.
:::

## Displaying User with ViewModel

Let's write a ViewModel component to display a user:

```kotlin
class UserViewModel(private val userService: UserService) : ViewModel() {

    fun sayHello(name: String): String {
        val user = userService.getUserOrNull(name)
        val message = userService.prepareHelloMessage(user)
        return "[UserViewModel] $message"
    }
}
```

> UserService is referenced in UserViewModel's constructor

We declare `UserViewModel` in our Koin module. We declare it as a `viewModel` definition, to not keep any instance in memory (avoid any leak with Android lifecycle):

```kotlin
val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
    viewModel<UserViewModel>()
}
```

## Injecting ViewModel in Jetpack Compose

With Jetpack Compose, we use `ComponentActivity` instead of `AppCompatActivity`, and we build our UI with composable functions instead of XML layouts.

The `UserViewModel` component will be created, resolving the `UserService` instance with it. To get it into our Compose UI, we use the `koinViewModel()` function:

```kotlin
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
        topBar = {
            TopAppBar(
                title = { Text("Koin Sample") }
            )
        }
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

That's it, your Compose app is ready!

:::info
The `koinViewModel()` function retrieves a ViewModel instance from Koin and automatically binds it to the Compose lifecycle. This is the Compose-specific way to inject ViewModels, replacing the `by viewModel()` delegate used in traditional Android Views.
:::

### Key Compose Concepts

- **ComponentActivity**: Base class for Compose apps (instead of AppCompatActivity)
- **setContent**: Sets the Composable content as the activity's UI
- **@Composable**: Functions that build UI declaratively
- **remember & mutableStateOf**: Compose state management for reactive UI updates
- **koinViewModel()**: Koin's Compose integration for ViewModel injection

## Start Koin

We need to start Koin with our Android application. Just call the `startKoin()` function in the application's main entry point, our `MainApplication` class:

```kotlin
class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
```

:::info
The `modules()` function in `startKoin` load the given list of modules
:::

## Koin module: DSL comparison

Here is the Koin module declaration using **Classic DSL** (manual wiring):

```kotlin
val appModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<UserService> { UserServiceImpl(get()) }
    viewModel { UserViewModel(get()) }
}
```

With **Compiler Plugin DSL** (auto-wiring at compile time):

```kotlin
val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
    viewModel<UserViewModel>()
}
```

:::tip
The Compiler Plugin DSL requires the [Koin Compiler Plugin](/docs/setup/compiler-plugin). It provides compile-time dependency resolution and cleaner syntax.
:::


## Compose vs XML Views

This tutorial demonstrates the same functionality as the [Android ViewModel tutorial](./android-viewmodel.md), but using Jetpack Compose instead of XML layouts:

| Aspect | XML Views | Jetpack Compose |
|--------|-----------|-----------------|
| Activity Base | `AppCompatActivity` | `ComponentActivity` |
| UI Definition | XML layout files | `@Composable` functions |
| ViewModel Injection | `by viewModel()` delegate | `koinViewModel()` function |
| State Management | LiveData/StateFlow | `remember` + `mutableStateOf` |
| UI Updates | View binding + observers | Automatic recomposition |

:::tip
For a version using Koin Annotations with Compose, see the [Compose Multiplatform Annotations tutorial](./compose-multiplatform-annotations.md).
:::