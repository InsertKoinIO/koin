---
title: Kotlin & Annotations
description: Build a small Kotlin console app with Koin Annotations, component scanning and a typed startKoin, checked by the Compiler Plugin.
---

In this tutorial you build a Kotlin console app that loads a list of users and prints a greeting. You learn how to annotate classes, scan them into a module, and start Koin from a `@KoinApplication` object. It takes about 10 minutes and needs a JDK 17 and Gradle.

This tutorial uses annotations. For the same app with the Compiler Plugin DSL, see [Kotlin](/docs/quickstart/kotlin). To compare the styles, see [Choosing your style](/docs/intro/choosing-your-style).

## Get the code

The full sample is in [koin-getting-started/kotlin-annotations](https://github.com/InsertKoinIO/koin-getting-started/tree/main/kotlin-annotations). Clone the repository and run it from the `kotlin-annotations` folder:

```bash
./gradlew run
```

## Gradle setup

The sample applies the Koin Compiler Plugin and adds `koin-core` and `koin-annotations`. Versions live in a version catalog:

```toml
# gradle/libs.versions.toml
[versions]
kotlin = "<KOTLIN_VERSION>"
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin" }
koin-test = { module = "io.insert-koin:koin-test", version.ref = "koin" }
koin-test-junit4 = { module = "io.insert-koin:koin-test-junit4", version.ref = "koin" }

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
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
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)

    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
}
```

The Compiler Plugin processes the annotations and needs Kotlin 2.3.20 or later. `koin-annotations` shares the Koin version. For details, see [Gradle setup](/docs/setup/gradle) and [Compiler Plugin setup](/docs/setup/compiler-plugin).

## The application

The app chains three components. `UserApplication` asks `UserService` for a user, and `UserService` reads from `UserRepository`:

```
User -> UserRepository -> UserService -> UserApplication
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

The repository keeps users in memory. Annotate the implementation with `@Singleton` to declare it as a singleton:

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

## Let's write the application class

`UserApplication` receives `UserService` through its constructor, loads the users and prints the greeting:

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

All three components are now annotated. Next, gather them in a module.

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

## Let's start Koin

Declare a `@KoinApplication` object and start Koin with the typed `startKoin<T>()`:

```kotlin
import org.koin.core.annotation.KoinApplication
import org.koin.mp.KoinPlatform
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
object KoinUserApplication

fun main() {
    startKoin<KoinUserApplication>()

    val userApplication = KoinPlatform.getKoin().get<UserApplication>()
    userApplication.sayHello("Alice")
}
```

`startKoin<KoinUserApplication>()` loads every `@Configuration` module, so you don't call `modules()`. The plugin validates the full graph at build time, and `./gradlew run` prints `Hello 'Alice' (alice@example.com)!`. See [Starting Koin](/docs/reference/koin-core/starting-koin) for the other options.

## Next steps

- [Core Concepts](/docs/intro/core-concepts): the mental model behind modules, definitions and injection.
- [Modules](/docs/reference/koin-core/modules): `@Module`, `@ComponentScan`, `@Configuration` and module includes.
- [Annotations Reference](/docs/reference/koin-annotations/annotations-inventory): every annotation and its parameters.
- [Testing](/docs/reference/koin-test/testing): write tests with Koin Test.
- [Tooling](/docs/tooling): IDE plugin and monitoring tools for Koin.
