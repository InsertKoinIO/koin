---
title: Ktor & Annotations
description: Build a Ktor web app with Koin Annotations, load modules with withConfiguration, and inject a service in your routes.
---

In this tutorial you build a Ktor web app with a `/hello` endpoint that greets a user picked from a list. You learn how to annotate classes, scan them into a module, and load them in the Koin Ktor plugin from a `@KoinApplication` object. It takes about 10 minutes and needs a JDK 17 and Gradle.

This tutorial uses annotations. For the same app with the Compiler Plugin DSL, see [Ktor](/docs/quickstart/ktor). To compare the styles, see [Choosing your style](/docs/intro/choosing-your-style).

## Get the code

The full sample is in [koin-getting-started/ktor-annotations](https://github.com/InsertKoinIO/koin-getting-started/tree/main/ktor-annotations). Clone the repository and run the `app` module from the `ktor-annotations` folder.

## Gradle setup

The sample applies the Koin Compiler Plugin and adds `koin-ktor`, `koin-annotations` and `koin-logger-slf4j`. Versions live in a version catalog:

```toml
# gradle/libs.versions.toml
[versions]
# Kotlin, Ktor and Logback entries omitted
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-ktor = { module = "io.insert-koin:koin-ktor", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin" }
koin-logger-slf4j = { module = "io.insert-koin:koin-logger-slf4j", version.ref = "koin" }
koin-test = { module = "io.insert-koin:koin-test", version.ref = "koin" }
koin-test-junit4 = { module = "io.insert-koin:koin-test-junit4", version.ref = "koin" }

[plugins]
koin-compiler = { id = "io.insert-koin.compiler.plugin", version.ref = "koin-plugin" }
```

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.koin.compiler)
    application
}

dependencies {
    implementation(libs.ktor.server)
    implementation(libs.logback)
    implementation(libs.koin.ktor)
    implementation(libs.koin.annotations)
    implementation(libs.koin.logger.slf4j)

    testImplementation(libs.ktor.test)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
}
```

The Compiler Plugin processes the annotations and needs Kotlin 2.3.20 or later. `koin-annotations` shares the Koin version. For details, see [Gradle setup](/docs/setup/gradle) and [Compiler Plugin setup](/docs/setup/compiler-plugin).

## The application

The `/hello` route asks `UserService` for a user, and `UserService` reads from `UserRepository`:

```
User -> UserRepository -> UserService -> /hello route
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

Koin binds `UserRepositoryImpl` to its supertypes, so a request for `UserRepository` gets this instance. `@Singleton` comes from `org.koin.core.annotation` and is equivalent to `@Single` (see [Definitions](/docs/reference/koin-core/definitions)).

## Let's write the service

`UserService` loads the users and prepares the greeting. It is a `@Singleton` too:

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

## Let's install Koin and write the route

Declare a `@KoinApplication` object. Install the `Koin` plugin and load the configuration with `withConfiguration<T>()`, then inject `UserService` in the `/hello` route:

```kotlin
import org.koin.core.annotation.KoinApplication
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.plugin.module.dsl.withConfiguration

@KoinApplication
object KoinUserApplication

fun Application.main() {
    install(Koin) {
        slf4jLogger()
        withConfiguration<KoinUserApplication>()
    }

    val service by inject<UserService>()
    service.loadUsers()

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

`withConfiguration<KoinUserApplication>()` loads every `@Configuration` module, so you don't call `modules()`, and the plugin validates the full graph at build time. `slf4jLogger()` sends Koin logs to SLF4J. See [Koin for Ktor](/docs/reference/koin-ktor/ktor) for the Ktor-specific APIs.

## Let's start the server

Start an embedded Netty server that runs your application module:

```kotlin
fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        main()
    }.start(wait = true)
}
```

Run the app, then open these URLs:

- `http://localhost:8080/hello`: greets Alice, the default user.
- `http://localhost:8080/hello?name=Bob`: greets Bob.

## Next steps

- [Core Concepts](/docs/intro/core-concepts): the mental model behind modules, definitions and injection.
- [Koin for Ktor](/docs/reference/koin-ktor/ktor): install options, injection in routes and Ktor events.
- [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): every annotation and its parameters.
- [Ktor testing](/docs/reference/koin-ktor/ktor-testing): test your routes with `testApplication` and Koin.
- [Tooling](/docs/tooling): IDE plugin and monitoring tools for Koin.
