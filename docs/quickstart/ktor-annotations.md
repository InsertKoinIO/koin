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
data class User(val name : String)
```

We create a "Repository" component to manage the list of users (add users or find one by name). Here below, the `UserRepository` interface and its implementation:

```kotlin
interface UserRepository {
    fun findUser(name : String): User?
    fun addUsers(users : List<User>)
}

class UserRepositoryImpl : UserRepository {

    private val _users = arrayListOf<User>()

    override fun findUser(name: String): User? {
        return _users.firstOrNull { it.name == name }
    }

    override fun addUsers(users : List<User>) {
        _users.addAll(users)
    }
}
```

## The Koin module

Use the `@Module` annotation to declare a Koin module, from a given Kotlin class. A Koin module is the place where we define all our components to be injected.

```kotlin
@Module
@ComponentScan("org.koin.sample")
class AppModule
```

The `@ComponentScan("org.koin.sample")` will help scan annotated classes from targeted package.

Let's declare our first component. We want a singleton of `UserRepository`, by creating an instance of `UserRepositoryImpl`. We tag it `@Single`

```kotlin
@Single
class UserRepositoryImpl : UserRepository
```

## The UserService Component

Let's write the UserService component to request the default user:

```kotlin
class UserService(private val userRepository: UserRepository) {

    fun getDefaultUser() : User = userRepository.findUser(DefaultData.DEFAULT_USER.name) ?: error("Can't find default user")
}
```

> UserRepository is referenced in UserPresenter`s constructor

We declare `UserService` in our Koin module. We tag with `@Single` annotation:

```kotlin
@Single
class UserService(private val userRepository: UserRepository)
```

## HTTP Controller

Finally, we need an HTTP Controller to create the HTTP Route. In Ktor is will be expressed through an Ktor extension function:

```kotlin
fun Application.main() {

    // Lazy inject HelloService
    val service by inject<UserService>()

    // Routing section
    routing {
        get("/hello") {
            call.respondText(service.sayHello())
        }
    }
}
```

Check that your `application.conf` is configured like below, to help start the `Application.main` function:

```kotlin
ktor {
    deployment {
        port = 8080

        // For dev purpose
        //autoreload = true
        //watch = [org.koin.sample]
    }

    application {
        modules = [ org.koin.sample.UserApplicationKt.main ]
    }
}
```

## Start and Inject

Finally, let's start Koin from Ktor:

```kotlin
fun Application.main() {
    install(Koin) {
        slf4jLogger()
        modules(AppModule().module)
    }

    // Lazy inject HelloService
    val service by inject<UserService>()
    service.saveDefaultUsers()

    // Routing section
    routing {
        get("/hello") {
            call.respondText(service.sayHello())
        }
    }
}
```

By writing the `AppModule().module` we use a generated extension on `AppModule` class.

Let's start Ktor:

```kotlin
fun main(args: Array<String>) {
    // Start Ktor
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}
```

That's it! You're ready to go. Check the `http://localhost:8080/hello` url!
