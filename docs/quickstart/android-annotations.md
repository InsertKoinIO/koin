---
title: Android & Annotations
description: Build an Android app with Koin Annotations, a ViewModel with its own scope, and a typed startKoin in Application, checked by the Compiler Plugin.
---

In this tutorial you build an Android app that greets a user picked from a list. You learn how to annotate classes, scan them into a module, give a ViewModel its own scoped dependency, and start Koin from a `@KoinApplication` class. It takes about 10 minutes and needs Android Studio.

This tutorial uses annotations. For the same app with the Compiler Plugin DSL, see [Android](/docs/quickstart/android) and [Android ViewModel](/docs/quickstart/android-viewmodel). To compare the styles, see [Choosing your style](/docs/intro/choosing-your-style).

## Get the code

The full sample is in [koin-getting-started/android-annotations](https://github.com/InsertKoinIO/koin-getting-started/tree/main/android-annotations). Clone the repository and open the `android-annotations` folder in Android Studio.

## Gradle setup

The sample applies the Koin Compiler Plugin and adds `koin-android` and `koin-annotations`. Versions live in a version catalog:

```toml
# gradle/libs.versions.toml
[versions]
# Kotlin, Android Gradle Plugin and AndroidX entries omitted
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin" }
koin-test-junit4 = { module = "io.insert-koin:koin-test-junit4", version.ref = "koin" }

[plugins]
koin-compiler = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }
```

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(libs.android.appcompat)
    implementation(libs.koin.android)
    implementation(libs.koin.annotations)

    testImplementation(libs.koin.test.junit4)
}
```

The Compiler Plugin processes the annotations and needs Kotlin 2.3.20 or later. `koin-annotations` shares the Koin version. Compile-time safety is on by default. For details, see [Gradle setup](/docs/setup/gradle) and [Compiler Plugin setup](/docs/setup/compiler-plugin).

## The application

The Activity asks a ViewModel for a greeting. The ViewModel uses `UserService`, which reads from `UserRepository`, and owns a `UserPresenter` scoped to its lifecycle:

```
User -> UserRepository -> UserService -> UserViewModel (+ UserPresenter) -> MainActivity
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

The repository keeps users in memory. Annotate the implementation with `@Singleton`:

```kotlin
import org.koin.core.annotation.Singleton

interface UserRepository {
    fun findUserOrNull(name: String): User?
    fun addUsers(users: List<User>)
}

@Singleton
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

Koin binds `UserRepositoryImpl` to its supertypes, so a request for `UserRepository` gets this instance. `@Singleton` is equivalent to `@Single` (see [Definitions](/docs/reference/koin-core/definitions)).

## Let's write the service

`UserService` loads the users when it is created and prepares the greeting:

```kotlin
interface UserService {
    fun getUserOrNull(name: String = DEFAULT_USER.name): User?
    fun loadUsers()
    fun prepareHelloMessage(user: User?): String
}

@Singleton
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

The Compiler Plugin reads the constructor and passes the `UserRepository` instance for you.

## Let's write the presenter and the ViewModel

The presenter formats a greeting. Annotate it with `@ViewModelScope`, so each ViewModel gets its own instance:

```kotlin
import org.koin.core.annotation.ViewModelScope

@ViewModelScope
class UserPresenter(
    private val userService: UserService
) {

    fun sayHello(name: String): String {
        val user = userService.getUserOrNull(name)
        val message = userService.prepareHelloMessage(user)
        return "[UserPresenter] $message"
    }
}
```

The ViewModel is annotated with `@KoinViewModel`, and with `@ViewModelScope` so it can receive the scoped presenter:

```kotlin
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.ViewModelScope

@ViewModelScope
@KoinViewModel
class UserViewModel(
    private val userService: UserService,
    private val userPresenter: UserPresenter
) : ViewModel() {

    fun sayHello(name: String): String {
        val user = userService.getUserOrNull(name)
        val message = userService.prepareHelloMessage(user)
        return "[UserViewModel] $message"
    }
}
```

The presenter lives as long as the ViewModel and is released when the ViewModel is cleared. ViewModel scopes are explained in [ViewModel](/docs/reference/koin-core/viewmodel), and scopes in general in [Scopes](/docs/reference/koin-core/scopes).

## Let's declare the Koin module

A module groups definitions (see [Core Concepts](/docs/intro/core-concepts)). Declare a module class that scans the sample package:

```kotlin
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module

@Module
@ComponentScan("org.koin.sample")
@Configuration
class AppModule
```

`@ComponentScan` registers every annotated class in `org.koin.sample` and its subpackages. `@Configuration` makes the module discoverable by a `@KoinApplication` entry point. Both are described in [Modules](/docs/reference/koin-core/modules).

## Let's inject the ViewModel in the Activity

Use the `by viewModel()` delegate in `MainActivity`:

```kotlin
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val sayHelloButton = findViewById<Button>(R.id.sayHelloButton)
        val textField = findViewById<TextView>(R.id.text)

        sayHelloButton.setOnClickListener {
            val userName = nameInput.text.toString().trim().ifEmpty { "Alice" }
            textField.text = viewModel.sayHello(userName)
        }
    }
}
```

`by viewModel()` creates the ViewModel factory for you and binds the instance to the Activity lifecycle. The other delegates are in [Android ViewModel](/docs/reference/koin-android/viewmodel).

## Let's start Koin

Annotate your `Application` class with `@KoinApplication` and start Koin with the typed `startKoin<T>()`. Register the class in `AndroidManifest.xml`:

```kotlin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.KoinViewModelScopeApi
import org.koin.core.logger.Level
import org.koin.core.option.viewModelScopeFactory
import org.koin.plugin.module.dsl.startKoin

@OptIn(KoinViewModelScopeApi::class)
@KoinApplication
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin<MainApplication> {
            androidContext(this@MainApplication)
            androidLogger(level = Level.DEBUG)
            options(viewModelScopeFactory())
        }
    }
}
```

`startKoin<MainApplication>` loads every `@Configuration` module, so you don't call `modules()`. `viewModelScopeFactory()` tells Koin to create a scope for each ViewModel, which `@ViewModelScope` needs. It is an opt-in API (`@KoinViewModelScopeApi`). See [Starting Koin on Android](/docs/reference/koin-android/start) for the other options.

## Let's check the graph at build time

Build the project. The Compiler Plugin validates the full graph: a missing dependency, a qualifier mismatch or a `by viewModel()` call on an undeclared ViewModel fails the build with the file and line.

Run the app, type a name and tap the button to see the greeting. What the plugin checks is listed in [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety).

## Next steps

- [Core Concepts](/docs/intro/core-concepts): the mental model behind modules, definitions and injection.
- [Android scopes](/docs/reference/koin-android/scope): Activity, Fragment and ViewModel scopes on Android.
- [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): every annotation and its parameters.
- [Instrumented testing](/docs/reference/koin-android/instrumented-testing): test your Android app with Koin.
- [Tooling](/docs/tooling): IDE plugin and monitoring tools for Koin.
