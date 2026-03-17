---
title: Ktor & Annotations
---

> Ktor is a framework for building asynchronous servers and clients in connected systems using the powerful Kotlin programming language. We will use Ktor here, to build a simple web application.

Let's go 🚀

:::note
update - 2024-10-21
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/ktor-annotations)
:::

## Gradle Setup

First, add the Koin dependency like below:

```kotlin
plugins {

    id("com.google.devtools.ksp") version kspVersion
}

dependencies {
    // Koin for Kotlin apps
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")

    implementation("io.insert-koin:koin-annotations:$koinAnnotationsVersion")
    ksp("io.insert-koin:koin-ksp-compiler:$koinAnnotationsVersion")
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

## The Koin module

Use the `@Module` annotation to declare a Koin module, from a given Kotlin class. A Koin module is the place where we define all our components to be injected.

```kotlin
@Module
@ComponentScan("org.koin.sample")
@Configuration
class AppModule
```

* `@Module` - Declares this as a Koin module
* `@ComponentScan("org.koin.sample")` - Scans and registers annotated classes from the package
* `@Configuration` - Enables automatic module discovery with `@KoinApplication`

:::note
This project uses Koin's `@Singleton` annotation (from `org.koin.core.annotation`) to declare singleton components.
:::

Let's declare our first component. We want a singleton of `UserRepository`, by creating an instance of `UserRepositoryImpl`. We tag it `@Singleton`:

```kotlin
@Singleton
class UserRepositoryImpl : UserRepository
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

We declare `UserService` with the `@Singleton` annotation:

## HTTP Controller and Koin Application

Finally, we need to create a `@KoinApplication` object and configure our HTTP routes:

```kotlin
@KoinApplication
object KoinUserApplication
```

The `@KoinApplication` annotation marks this as the entry point for Koin's annotation-based configuration. The KSP processor generates configuration that can be used with `withConfiguration<T>()` to initialize Koin.

## Start and Inject

Now let's configure the Ktor application with Koin:

```kotlin
fun Application.main() {
    // Install Koin with generated configuration
    install(Koin) {
        slf4jLogger()
        withConfiguration<KoinUserApplication>()
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

**Key Points:**
* `withConfiguration<KoinUserApplication>()` - Uses the generated Koin configuration from the annotated application object
* No need to manually call `modules(AppModule().module)` - it's included automatically!
* The `/hello` endpoint accepts an optional `name` query parameter

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

:::info
The `@KoinApplication` annotation with `@Configuration` on the module automatically discovers and loads all annotated dependencies at compile time.
:::
