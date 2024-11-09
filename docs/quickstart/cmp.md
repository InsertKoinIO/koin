---
title: Compose Multiplatform - Shared UI
---

> This tutorial lets you write an Android application and use Koin dependency injection to retrieve your components.
> You need around __15 min__ to do the tutorial.

:::note
update - 2024-10-21
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/ComposeMultiplatform)
:::

## Application Overview

The idea of the application is to manage a list of users, and display it in our native UI, witha shared ViewModel:

`Users -> UserRepository -> Shared Presenter -> Compose UI`

## The "User" Data

> All the common/shared code is located in `shared` Gradle project

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

## The Shared Koin module

Use the `module` function to declare a Koin module. A Koin module is the place where we define all our components to be injected.

Let's declare our first component. We want a singleton of `UserRepository`, by creating an instance of `UserRepositoryImpl`

```kotlin
module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
}
```

## The Shared ViewModel

Let's write a ViewModel component to display a user:

```kotlin
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun sayHello(name : String) : String{
        val foundUser = repository.findUser(name)
        val platform = getPlatform()
        return foundUser?.let { "Hello '$it' from ${platform.name}" } ?: "User '$name' not found!"
    }
}
```

> UserRepository is referenced in UserPresenter`s constructor

We declare `UserViewModel` in our Koin module. We declare it as a `viewModelOf` definition, to not keep any instance in memory and let the native system hold it:

```kotlin
val appModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    viewModelOf(::UserViewModel)
}
```

:::note
The Koin module is available as function to run (`appModule` here), to be easily runned from iOS side, with `initKoin()` function. 
:::


## Native Component

The following native component is defined in Android and iOS:

```kotlin
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
```

Both get local platform implementation


## Injecting in Compose

> All the Common Compose app is located in `commonMain` from `composeApp` Gradle module:

The `UserViewModel` component will be created, resolving the `UserRepository` instance with it. To get it into our Activity, let's inject it with the `koinViewModel` or `koinNavViewModel` compose function: 

```kotlin
@Composable
fun MainScreen() {

    MaterialTheme {

        val userViewModel = koinViewModel<UserViewModel>()
        
        //...
    }
}
```

That's it, your app is ready.

We need to start Koin with our Android application. Just call the `KoinApplication()` function in the compose application function `App`:

```kotlin
fun App() {
    
    KoinApplication(
        application = {
            modules(appModule)
        }
    )
{
// Compose content
}
}
```

:::info
The `modules()` function load the given list of modules
:::


## Compose app in iOS

> All the iOS app is located in `iosMain` folder

The `MainViewController.kt` is ready to start Compose for iOS:

```kotlin
// Koin.kt

fun MainViewController() = ComposeUIViewController { App() }
```
