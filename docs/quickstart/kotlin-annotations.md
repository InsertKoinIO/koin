---
title: Kotlin & Annotations
---

> This tutorial lets you write a Kotlin application and use Koin dependency injection with annotations to retrieve your components.
> You need around __10 min__ to do the tutorial.

:::note
update - 2024-11-12
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/kotlin-annotations)
:::

## Setup

First, check that the Koin annotations dependencies are added like below:

```groovy
plugins {
    id("com.google.devtools.ksp") version kspVersion
}

dependencies {
    // Koin for Kotlin apps
    implementation("io.insert-koin:koin-core:$koin_version")

    // Koin Annotations
    implementation("io.insert-koin:koin-annotations:$koin_annotations_version")
    ksp("io.insert-koin:koin-ksp-compiler:$koin_annotations_version")
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

:::note
This project uses Koin's `@Singleton` annotation (from `org.koin.core.annotation`) to declare singleton components.
:::

## The Koin module

Use the `@Module` annotation to declare a Koin module:

```kotlin
@Module
@ComponentScan("org.koin.sample")
@Configuration
class AppModule
```

* `@Module` - Declares this as a Koin module
* `@ComponentScan("org.koin.sample")` - Scans and registers annotated classes from the package
* `@Configuration` - Enables automatic module discovery with `@KoinApplication`

Let's declare our components by adding the `@Singleton` annotation:

```kotlin
@Singleton
class UserRepositoryImpl : UserRepository {
    // ...
}
```

## The UserService Component

Let's write the UserService component to manage user operations:

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

We declare `UserService` with the `@Singleton` annotation.

## The UserApplication

The `UserApplication` class uses constructor injection to receive the `UserService`:

```kotlin
@Singleton
class UserApplication(
    private val userService: UserService
) {

    init {
        userService.loadUsers()
    }

    fun sayHello(name: String) {
        val user = userService.getUserOrNull(name)
        val message = userService.prepareHelloMessage(user)
        println(message)
    }
}
```

:::info
Constructor injection is the preferred way to inject dependencies. Koin will automatically resolve and inject the `UserService` when creating `UserApplication`.
:::

## Koin Application Object

Create a `@KoinApplication` object to mark the entry point for Koin's annotation-based configuration:

```kotlin
@KoinApplication
object KoinUserApplication
```

The `@KoinApplication` annotation works with the KSP processor to generate a `startKoin()` extension function for this object.

## Start Koin

We need to start Koin with our application. Just call the generated `startKoin()` function in the application's main entry point:

```kotlin
fun main() {
    KoinUserApplication.startKoin()

    val userApplication = KoinPlatform.getKoin().get<UserApplication>()
    userApplication.sayHello("Alice")
}
```

**Key Points:**
* `KoinUserApplication.startKoin()` - Generated function that automatically discovers and loads all modules
* No need to manually call `modules()` - all annotated dependencies are discovered at compile time!
* We retrieve the `UserApplication` instance from Koin using `KoinPlatform.getKoin().get<UserApplication>()`

:::info
The `@KoinApplication` annotation with `@Configuration` on the module automatically discovers and loads all annotated dependencies at compile time via KSP.
:::

## Annotations vs Compiler Plugin DSL

Here is how our annotations-based configuration compares to the Compiler Plugin DSL:

**With Annotations:**
```kotlin
@Module
@ComponentScan("org.koin.sample")
@Configuration
class AppModule

@Singleton
class UserApplication(private val userService: UserService)

@Singleton
class UserRepositoryImpl : UserRepository

@Singleton
class UserServiceImpl(private val userRepository: UserRepository) : UserService
```

**Compiler Plugin DSL (from kotlin.md):**
```kotlin
val appModule = module {
    single<UserApplication>()
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

Both approaches achieve the same result:
- **Annotations**: Compile-time verification via KSP, automatic module discovery
- **Compiler Plugin DSL**: Auto-wiring at compile time, cleaner `single<T>()` syntax
