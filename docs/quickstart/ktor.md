---
title: Ktor
---

> Ktor is a framework for building asynchronous servers and clients in connected systems using the powerful Kotlin programming language. We will use Ktor here, to build a simple web application.

Let's go 🚀

:::note
update - 2024-10-21
:::

:::tip
Looking for the **annotations version** of this tutorial? Check out [Ktor & Annotations](./ktor-annotations.md) which uses Koin Annotations with Jakarta `@Singleton` for compile-time verification.
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/ktor)
:::

## Gradle Setup

First, add the Koin dependency like below:

```kotlin
dependencies {
    // Koin for Kotlin apps
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
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

## HTTP Controller

Finally, we need an HTTP Controller to create the HTTP Route. In Ktor it will be expressed through a Ktor extension function:

```kotlin
fun Application.main() {

    // Lazy inject UserService
    val service by inject<UserService>()
    service.loadUsers()

    // Routing section
    routing {
        get("/hello") {
            val userName = call.queryParameters["name"] ?: "Alice"
            val user = service.getUserOrNull(userName)
            val message = service.prepareHelloMessage(user)
            call.respondText(message)
        }
    }
}
```

The `/hello` endpoint accepts an optional `name` query parameter. If not provided, it defaults to "Alice".

Example requests:
- `http://localhost:8080/hello` - Greets Alice (default)
- `http://localhost:8080/hello?name=Bob` - Greets Bob

## Declare your dependencies

Let's assemble our components with a Koin module:

```kotlin
val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

## Start and Inject

Finally, let's start Koin from Ktor:

```kotlin
fun Application.main() {
    // Install Koin
    install(Koin) {
        modules(appModule)
    }

    // Lazy inject UserService
    val service by inject<UserService>()
    service.loadUsers()

    // Routing section
    routing {
        get("/hello") {
            val userName = call.queryParameters["name"] ?: "Alice"
            val user = service.getUserOrNull(userName)
            val message = service.prepareHelloMessage(user)
            call.respondText(message)
        }
    }
}
```

Let's start Ktor:

```kotlin
fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        main()
    }.start(wait = true)
}
```

That's it! You're ready to go. Check these URLs:
- `http://localhost:8080/hello` - Greets Alice (default user)
- `http://localhost:8080/hello?name=Bob` - Greets Bob
