---
title: Kotlin
description: Build a small Kotlin console app with Koin and the Compiler Plugin DSL, from module declaration to starting Koin.
---

In this tutorial you build a Kotlin console app that loads a list of users and prints a greeting. You learn how to declare a Koin module, bind implementations to interfaces, and start Koin. It takes about 10 minutes and needs a JDK 17 and Gradle.

This tutorial uses the Compiler Plugin DSL. For the same app with annotations, see [Kotlin & Annotations](/docs/quickstart/kotlin-annotations). To compare the styles, see [Choosing your style](/docs/intro/choosing-your-style).

## Get the code

The full sample is in [koin-getting-started/kotlin](https://github.com/InsertKoinIO/koin-getting-started/tree/main/kotlin). Clone the repository and run it from the `kotlin` folder:

```bash
./gradlew run
```

## Gradle setup

The sample applies the Koin Compiler Plugin and adds `koin-core`. Versions live in a version catalog:

```toml
# gradle/libs.versions.toml
[versions]
kotlin = "<KOTLIN_VERSION>"
koin = "<KOIN_VERSION>"
koin-plugin = "<KOIN_PLUGIN_VERSION>"

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
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

    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
}
```

The Compiler Plugin needs Kotlin 2.3.20 or later. For current versions and the BOM, see [Gradle setup](/docs/setup/gradle). For plugin options, see [Compiler Plugin setup](/docs/setup/compiler-plugin).

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

## Let's declare the Koin module

A module is where you declare the components Koin creates (see [Core Concepts](/docs/intro/core-concepts)). Declare `UserRepositoryImpl` as a singleton, available as `UserRepository`:

```kotlin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
}
```

Any component that asks for a `UserRepository` now gets the same `UserRepositoryImpl` instance. `single` and `bind` are described in [Definitions](/docs/reference/koin-core/definitions).

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

Add it to the module:

```kotlin
val appModule = module {
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

`UserServiceImpl` takes a `UserRepository` in its constructor. The Compiler Plugin reads that constructor and passes the `UserRepositoryImpl` instance for you.

## Let's write the application class

`UserApplication` receives `UserService` through its constructor, loads the users and prints the greeting:

```kotlin
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

Declare it in the module too:

```kotlin
val appModule = module {
    single<UserApplication>()
    single<UserRepositoryImpl>() bind UserRepository::class
    single<UserServiceImpl>() bind UserService::class
}
```

All three components are now in the module. The plugin checks at build time that each constructor parameter has a definition.

## Let's start Koin

Start Koin in `main()` with your module, then get `UserApplication` from the container:

```kotlin
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

fun main() {
    startKoin {
        modules(appModule)
    }

    val userApplication = KoinPlatform.getKoin().get<UserApplication>()
    userApplication.sayHello("Alice")
}
```

Run `./gradlew run` and the app prints `Hello 'Alice' (alice@example.com)!`. `startKoin` and its options are covered in [Starting Koin](/docs/reference/koin-core/starting-koin).

## Next steps

- [Core Concepts](/docs/intro/core-concepts): the mental model behind modules, definitions and injection.
- [Definitions](/docs/reference/koin-core/definitions): every definition type and binding option.
- [JUnit Tests](/docs/quickstart/junit-test): test this same app with Koin Test.
- [Compile-Time Safety](/docs/reference/koin-compiler/compile-safety): what the Compiler Plugin checks in your graph.
- [Tooling](/docs/tooling): IDE plugin and monitoring tools for Koin.
