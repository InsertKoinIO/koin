---
title: Kotlin
---

> This tutorial lets you write a Kotlin application and use Koin dependency injection to retrieve your components.
> You need around __10 min__ to do the tutorial.

:::note
update - 2024-10-21
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
    compile "io.insert-koin:koin-core:$koin_version"
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

Use the `module` function to declare a Koin module. A Koin module is the place where we define all our components to be injected.

```kotlin
val appModule = module {
    
}
```

Let's declare our first component. We want a singleton of `UserRepository`, by creating an instance of `UserRepositoryImpl`

```kotlin
val appModule = module {
    single<UserRepository> { UserRepositoryImpl() }
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

We declare `UserService` in our Koin module. We declare it as a `single` definition:

```kotlin
val appModule = module {
     single<UserRepository> { UserRepositoryImpl() }
     single { UserService(get()) }
}
```

> The `get()` function allow to ask Koin to resolve the needed dependency.

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
fun main() {
    startKoin {
        modules(appModule)
    }

    UserApplication().sayHello()
}
```

:::info
The `modules()` function in `startKoin` load the given list of modules
:::

## Koin module: classic or constructor DSL?

Here is the Koin module declaration for our app:

```kotlin
val appModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single { UserService(get()) }
}
```

We can write it in a more compact way, by using constructors:

```kotlin
val appModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::UserService)
}
```
