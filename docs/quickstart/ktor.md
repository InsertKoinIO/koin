---
title: Ktor
description: Build a Ktor web app with Koin and the Compiler Plugin DSL, install the Koin plugin and inject a service in your routes.
---

In this tutorial you build a Ktor web app with a `/hello` endpoint that greets a user picked from a list. You learn how to declare a Koin module, install Koin as a Ktor plugin, and inject a service in your application. It takes about 10 minutes and needs a JDK 17 and Gradle.

This tutorial uses the Compiler Plugin DSL. For the same app with annotations, see [Ktor & Annotations](/docs/quickstart/ktor-annotations). To compare the styles, see [Choosing your style](/docs/intro/choosing-your-style).

## Get the code

The full sample is in [koin-getting-started/ktor](https://github.com/InsertKoinIO/koin-getting-started/tree/main/ktor). Clone the repository and run it from the `ktor` folder:

```bash
./gradlew run
```

## Gradle setup

The sample applies the Koin Compiler Plugin and adds `koin-ktor` and `koin-logger-slf4j`. Versions live in a version catalog:

```toml
# gradle/libs.versions.toml
[versions]
# Kotlin, Ktor and Logback entries omitted
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-ktor = { module = "io.insert-koin:koin-ktor", version.ref = "koin" }
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
    implementation(libs.koin.logger.slf4j)

    testImplementation(libs.ktor.test)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
}
```

The Compiler Plugin needs Kotlin 2.3.20 or later. For current versions and the BOM, see [Gradle setup](/docs/setup/gradle). For plugin options, see [Compiler Plugin setup](/docs/setup/compiler-plugin).

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

`UserService` loads the users and prepares the greeting:

```kotlin
interface UserService {
    fun getUserOrNull(name: String = DEFAULT_USER.name): User?
    fun loadUsers()
    fun prepareHelloMessage(user: User?): String
}

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

## Let's install Koin and write the route

Install the `Koin` plugin in your Ktor application with your module, then inject `UserService` and use it in the `/hello` route:

```kotlin
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun Application.main() {
    install(Koin) {
        modules(appModule)
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

`install(Koin)` starts Koin with the Ktor application, and `inject()` is available on `Application`, `Route` and `ApplicationCall`. To send Koin logs to SLF4J, add `slf4jLogger()` inside `install(Koin)`. See [Koin for Ktor](/docs/reference/koin-ktor/ktor) for the Ktor-specific APIs.

## Let's start the server

Start an embedded Netty server that runs your application module:

```kotlin
fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        main()
    }.start(wait = true)
}
```

Run `./gradlew run`, then open these URLs:

- `http://localhost:8080/hello`: greets Alice, the default user.
- `http://localhost:8080/hello?name=Bob`: greets Bob.

## Next steps

- [Core Concepts](/docs/intro/core-concepts): the mental model behind modules, definitions and injection.
- [Koin for Ktor](/docs/reference/koin-ktor/ktor): install options, injection in routes and Ktor events.
- [Request Scopes](/docs/reference/koin-ktor/ktor-scopes): instances that live for one HTTP request.
- [Ktor testing](/docs/reference/koin-ktor/ktor-testing): test your routes with `testApplication` and Koin.
- [Tooling](/docs/tooling): IDE plugin and monitoring tools for Koin.
