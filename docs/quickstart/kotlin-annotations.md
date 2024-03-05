---
title: Kotlin - Annotations
---

> This tutorial lets you write a Kotlin application and use Koin dependency injection to retrieve your components.
> You need around __10 min__ to do the tutorial.

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/kotlin-annotations)
:::

## Setup

Let's configure the KSP plugin like below in Gradle:

```groovy
plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id 'org.jetbrains.kotlin.jvm' version "$kotlin_version"
    // Apply the application plugin to add support for building a CLI application in Java.
    id "com.google.devtools.ksp" version "$ksp_version"
    id 'application'
}

// KSP - To use generated sources
sourceSets.main {
    java.srcDirs("build/generated/ksp/main/kotlin")
}
```

Let's setup the dependencies like below: 

```groovy
dependencies {

    // Koin
    implementation "io.insert-koin:koin-core:$koin_version"
    implementation "io.insert-koin:koin-annotations:$koin_ksp_version"
    ksp "io.insert-koin:koin-ksp-compiler:$koin_ksp_version"
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

Let's declare a `AppModule` module class like below. 

```kotlin
@Module
@ComponentScan("org.koin.sample")
class AppModule
```

* We use the `@Module` to declare our class as Koin module
* The `@ComponentScan("org.koin.sample")` allow to scann any Koin definition in `"org.koin.sample"`package

Let's simply add `@Single` on `UserRepositoryImpl` class to declare it as singleton:

```kotlin
@Single
class UserRepositoryImpl : UserRepository {
    // ...
}
```

## The UserService Component

Let's write the UserService component to request the default user:

```kotlin
class UserService(private val userRepository: UserRepository) {

    fun getDefaultUser() : User = userRepository.findUser(DefaultData.DEFAULT_USER.name) ?: error("Can't find default user")
}
```

> UserRepository is referenced in UserPresenter`s constructor

Let's simply add `@Single` on `UserService` class to declare it as singleton:

```kotlin
@Single
class UserService(private val userRepository: UserRepository) {
    // ...
}
```

## Injecting Dependencies in UserApplication

The `UserApplication` class will help bootstrap instances out of Koin. It will resolve the `UserService`, thanks to `KoinComponent` interface. This allows to inject it with the `by inject()` delegate function: 

```kotlin
class UserApplication : KoinComponent {

    private val userService : UserService by inject()

    // display our data
    fun sayHello(){
        val user = userService.getDefaultUser()
        val message = "Hello '$user'!"
        println(message)
    }
}
```

That's it, your app is ready.

:::info
The `by inject()` function allows us to retrieve Koin instances, in any class that extends `KoinComponent`
:::


## Start Koin

We need to start Koin with our application. Just call the `startKoin()` function in the application's main entry point, our `main` function:

```kotlin
// generated
import org.koin.ksp.generated.*

fun main() {
    startKoin {
        modules(AppModule().module)
    }

    UserApplication().sayHello()
}
```

The Koin module is generated from `AppModule` with the `.module` extension: Just use the `AppModule().module` expression to get the Koin module from the annotations.


:::info
The `import org.koin.ksp.generated.*` import is required to allow to use generated Koin module content
:::

## Compile Time Checks

Koin Annotations allows to check your Koin configuration at compile time. This is available by jusing the following Gradle option:

```groovy
ksp {
    arg("KOIN_CONFIG_CHECK","true")
}
```