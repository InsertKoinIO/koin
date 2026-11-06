---
title: Android
description: Build an Android app with Koin and the Compiler Plugin DSL, inject a presenter in an Activity with by inject(), and start Koin in Application.
---

In this tutorial you build an Android app that greets a user picked from a list. You learn how to declare a Koin module, inject a presenter in an Activity, and start Koin from your `Application` class. It takes about 10 minutes and needs Android Studio.

This tutorial uses the Compiler Plugin DSL. For the same app with annotations, see [Android & Annotations](/docs/quickstart/android-annotations). To compare the styles, see [Choosing your style](/docs/intro/choosing-your-style). The [Android ViewModel](/docs/quickstart/android-viewmodel) tutorial builds the same app with a ViewModel instead of a presenter.

## Get the code

The full sample is in [koin-getting-started/android](https://github.com/InsertKoinIO/koin-getting-started/tree/main/android). Clone the repository and open the `android` folder in Android Studio.

## Gradle setup

The sample applies the Koin Compiler Plugin and adds `koin-android`. Versions live in a version catalog:

```toml
# gradle/libs.versions.toml
[versions]
# Kotlin, Android Gradle Plugin and AndroidX entries omitted
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
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
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(libs.android.appcompat)
    implementation(libs.koin.android)

    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.koin.android.test)
}
```

The Compiler Plugin needs Kotlin 2.3.20 or later. For current versions and the BOM, see [Gradle setup](/docs/setup/gradle). For plugin options, see [Compiler Plugin setup](/docs/setup/compiler-plugin).

## The application

The Activity asks a presenter for a greeting. The presenter uses `UserService`, which reads from `UserRepository`:

```
User -> UserRepository -> UserService -> UserPresenter -> MainActivity
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

## Let's declare the Koin module

A module is where you declare the components Koin creates (see [Core Concepts](/docs/intro/core-concepts)). Declare both components as singletons, bound to their interfaces:

```kotlin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

Any component that asks for `UserService` now gets the same `UserServiceImpl` instance. `single` and `bind` are described in [Definitions](/docs/reference/koin-core/definitions).

## Let's write the presenter

The presenter turns a name into a greeting:

```kotlin
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

Declare it with `factory`, so each request creates a new instance and Koin keeps no reference to it:

```kotlin
import org.koin.plugin.module.dsl.factory

val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
    factory<UserPresenter>()
}
```

A `factory` presenter cannot outlive the Activity that uses it, which avoids leaking the Activity.

## Let's inject the presenter in the Activity

Use the `by inject()` delegate to get the presenter in `MainActivity`:

```kotlin
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val presenter: UserPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val sayHelloButton = findViewById<Button>(R.id.sayHelloButton)
        val textField = findViewById<TextView>(R.id.text)

        sayHelloButton.setOnClickListener {
            val userName = nameInput.text.toString().trim().ifEmpty { "Alice" }
            textField.text = presenter.sayHello(userName)
        }
    }
}
```

`by inject()` resolves the presenter lazily, the first time you use it. The other Android components that support it are listed in [Android Entry Points](/docs/reference/koin-android/entry-points).

## Let's start Koin

Start Koin in your `Application` class, and register that class in `AndroidManifest.xml`:

```kotlin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
```

`androidContext()` makes the Android `Context` available to your definitions, and `androidLogger()` sends Koin logs to Logcat. Run the app, type a name and tap the button to see the greeting. See [Starting Koin on Android](/docs/reference/koin-android/start) for the other options.

## Next steps

- [Core Concepts](/docs/intro/core-concepts): the mental model behind modules, definitions and injection.
- [Android ViewModel](/docs/quickstart/android-viewmodel): the same app with a ViewModel.
- [Android Entry Points](/docs/reference/koin-android/entry-points): inject in Fragments, Services, receivers and more.
- [Instrumented testing](/docs/reference/koin-android/instrumented-testing): test your Android app with Koin.
- [Tooling](/docs/tooling): IDE plugin and monitoring tools for Koin.
