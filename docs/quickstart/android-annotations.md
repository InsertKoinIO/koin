---
title: Android & Annotations
---

> This tutorial lets you write an Android application and use Koin dependency injection to retrieve your components.
> You need around __10 min__ to do the tutorial.

:::note
update - 2024-10-21
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/android-annotations)
:::

## Gradle Setup

Let's configure the KSP Plugin like this, and the following dependencies:

```groovy
plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    // ...

    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp)
}

// Compile time check
ksp {
    arg("KOIN_CONFIG_CHECK","true")
}
```

:::note
See `libs.versions.toml` for current versions
:::

## Application Overview

The idea of the application is to manage a list of users, and display it in our `MainActivity` class with a Presenter or a ViewModel:

> Users -> UserRepository -> UserService -> (Presenter or ViewModel) -> MainActivity

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

## The UserService Component

Let's write a service component to manage user operations:

```kotlin
interface UserService {
    fun getUserOrNull(name: String): User?
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

Let's declare a `AppModule` module class like below:

```kotlin
@Module
@ComponentScan("org.koin.sample")
@Configuration
class AppModule
```

* `@Module` - Declares this class as a Koin module
* `@ComponentScan("org.koin.sample")` - Automatically scans and registers all Koin definitions in the `"org.koin.sample"` package
* `@Configuration` - Enables automatic module discovery when used with `@KoinApplication`

With component scanning enabled, we simply add annotations to our classes:

```kotlin
@Singleton
class UserRepositoryImpl : UserRepository {
    // ...
}

@Singleton
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    // ...
}
```

The `@Singleton` annotation declares these classes as singletons in Koin.

## Displaying User with Presenter

Let's write a presenter component to display a user:

```kotlin
@Factory
class UserPresenter(private val userService: UserService) {

    fun sayHello(name: String): String {
        val user = userService.getUserOrNull(name)
        val message = userService.prepareHelloMessage(user)
        return "[UserPresenter] $message"
    }
}
```

> UserService is referenced in UserPresenter's constructor

We declare `UserPresenter` with the `@Factory` annotation, to create a new instance each time it's requested (avoids memory leaks with Android lifecycle):

```kotlin
@Factory
class UserPresenter(private val userService: UserService) {
    // ...
}
```

## Injecting Dependencies in Android

The `UserPresenter` component will be created, resolving the `UserService` instance with it. To get it into our Activity, let's inject it with the `by inject()` delegate function: 

```kotlin
class MainActivity : AppCompatActivity() {

    private val presenter: UserPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        //...
    }
}
```

That's it, your app is ready.

:::info
The `by inject()` function allows us to retrieve Koin instances, in Android components runtime (Activity, fragment, Service...)
:::

## Start Koin

We need to start Koin with our Android application. With the `@KoinApplication` annotation, Koin automatically discovers and loads all modules marked with `@Configuration`:

```kotlin
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.KoinApplication
import org.koin.ksp.generated.*

@KoinApplication
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
        }
    }
}
```

**Key Points:**
* `@KoinApplication` - Automatically discovers all modules annotated with `@Module` and `@Configuration`
* No need to manually call `modules(AppModule().module)` - modules are loaded automatically!
* The `import org.koin.ksp.generated.*` import is required for generated Koin content
* You only need to configure Android-specific settings like `androidContext`

:::info
The `@KoinApplication` annotation works with `@Configuration` on your module to automatically discover and load all dependencies at compile time via KSP.
:::

## Displaying User with ViewModel

Let's write a ViewModel component to display a user:

```kotlin
@KoinViewModel
class UserViewModel(private val userService: UserService) : ViewModel() {

    fun sayHello(name: String): String {
        val user = userService.getUserOrNull(name)
        val message = userService.prepareHelloMessage(user)
        return "[UserViewModel] $message"
    }
}
```

> UserService is referenced in UserViewModel's constructor

The `UserViewModel` is tagged with `@KoinViewModel` annotation to declare the Koin ViewModel definition. This ensures proper lifecycle management and avoids memory leaks.


## Injecting ViewModel in Android

The `UserViewModel` component will be created, resolving the `UserService` instance with it. To get it into our Activity, let's inject it with the `by viewModel()` delegate function: 

```kotlin
class MainActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        //...
    }
}
```

## Compile Time Checks

Koin Annotations allows to check your Koin configuration at compile time. This is available by using the following Gradle option:

```groovy
ksp {
    arg("KOIN_CONFIG_CHECK","true")
}
```

:::note
This KSP-based option will be replaced by the **Koin Compiler Plugin** which will provide native compile-time safety. See [Compiler Plugin](/docs/setup/compiler-plugin) for the future approach.
:::

