---
title: Kotlin
---

> This tutorial lets you write a Kotlin application and use Koin dependency injection to retrieve your components.
> You need around __10 min__ to do the tutorial.

:::note
update - 2024-10-21
:::

:::tip
Looking for the **annotations version** of this tutorial? Check out [Kotlin & Annotations](./kotlin-annotations.md) which uses Koin Annotations for compile-time verification and automatic module discovery.
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/kotlin)
:::

## Setup

First, check that the `koin-core` dependency is added like below:

```groovy
dependencies {
    
    // Koin for Kotlin apps
    implementation "io.insert-koin:koin-core:$koin_version"
}
```

## Application Overview

The idea of the application is to manage a list of users, and display it in our `UserApplication` class:

> Users -> UserRepository -> UserService -> UserApplication

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

## The Koin module

Use the `module` function to declare a Koin module. A Koin module is the place where we define all our components to be injected.

```kotlin
val appModule = module {

}
```

Let's declare our first component. We want a singleton of `UserRepository`, by creating an instance of `UserRepositoryImpl`

```kotlin
val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
}
```

:::info
This tutorial uses the **Koin Compiler Plugin DSL** (`single<T>()`) which provides auto-wiring at compile time. See [Compiler Plugin Setup](/docs/setup/compiler-plugin) for configuration.
:::

## The UserService Component

Let's write the UserService component to manage user operations:

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

> UserRepository is referenced in UserServiceImpl's constructor

We declare `UserService` in our Koin module. We declare it as a `single` definition:

```kotlin
val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

## Injecting Dependencies in UserApplication

The `UserApplication` class will help bootstrap instances out of Koin. It will resolve the `UserService` through constructor injection:

```kotlin
class UserApplication(
    private val userService: UserService
) {

    init {
        userService.loadUsers()
    }

    // display our data
    fun sayHello(name: String) {
        val user = userService.getUserOrNull(name)
        val message = userService.prepareHelloMessage(user)
        println(message)
    }
}
```

That's it, your app is ready.

:::info
Constructor injection is the preferred way to inject dependencies in Kotlin applications. Koin will automatically resolve and inject the `UserService` when creating `UserApplication`.
:::


## Start Koin

We need to start Koin with our application and add `UserApplication` to our module. Just call the `startKoin()` function in the application's main entry point, our `main` function:

```kotlin
val appModule = module {
    single<UserApplication>()
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}

fun main() {
    startKoin {
        modules(appModule)
    }

    val userApplication = KoinPlatform.getKoin().get<UserApplication>()
    userApplication.sayHello("Alice")
}
```

:::info
The `modules()` function in `startKoin` loads the given list of modules. We retrieve the `UserApplication` instance from Koin using `KoinPlatform.getKoin().get<UserApplication>()`.
:::

## Koin module: DSL comparison

Here is the Koin module declaration using **Classic DSL** (manual wiring):

```kotlin
val appModule = module {
    single { UserApplication(get()) }
    single<UserRepository> { UserRepositoryImpl() }
    single<UserService> { UserServiceImpl(get()) }
}
```

With **Compiler Plugin DSL** (auto-wiring at compile time):

```kotlin
val appModule = module {
    single<UserApplication>()
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

:::tip
The Compiler Plugin DSL requires the [Koin Compiler Plugin](/docs/setup/compiler-plugin). It provides compile-time dependency resolution and cleaner syntax.
:::
