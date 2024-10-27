---
title: Kotlin Multiplatform - No shared UI
---

> This tutorial lets you write an Android application and use Koin dependency injection to retrieve your components.
> You need around __15 min__ to do the tutorial.

:::note
update - 2024-10-21
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/KotlinMultiplatform)
:::

## Application Overview

The idea of the application is to manage a list of users, and display it in our native UI, witha shared Presenter:

`Users -> UserRepository -> Shared Presenter -> Native UI`

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

## The Shared Presenter

Let's write a presenter component to display a user:

```kotlin
class UserPresenter(private val repository: UserRepository) {

    fun sayHello(name : String) : String{
        val foundUser = repository.findUser(name)
        val platform = getPlatform()
        return foundUser?.let { "Hello '$it' from ${platform.name}" } ?: "User '$name' not found!"
    }
}
```

> UserRepository is referenced in UserPresenter`s constructor

We declare `UserPresenter` in our Koin module. We declare it as a `factoryOf` definition, to not keep any instance in memory and let the native system hold it:

```kotlin
val appModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    factoryOf(::UserPresenter)
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


## Injecting in Android

> All the Android app is located in `androidApp` Gradle project

The `UserPresenter` component will be created, resolving the `UserRepository` instance with it. To get it into our Activity, let's inject it with the `koinInject` compose function: 

```kotlin
// in App()

val greeting = koinInject<UserPresenter>().sayHello("Koin")

Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
    Image(painterResource(Res.drawable.compose_multiplatform), null)
    Text("Compose: $greeting")
}
```

That's it, your app is ready.

:::info
The `koinInject()` function allows us to retrieve Koin instances, in Android Compose runtime
:::

We need to start Koin with our Android application. Just call the `KoinApplication()` function in the compose application function `App`:

```kotlin
fun App() {
    
    KoinApplication(application = koinAndroidConfiguration(LocalContext.current)){
        // ...
    }
}
```

We gather Koin android configuration, from the shared KMP configuration:

```kotlin
// Android config
fun koinAndroidConfiguration(context: Context) : KoinAppDeclaration = {
    androidContext(context)
    androidLogger()
    koinSharedConfiguration()
}
```

:::note
We get the current Android context from Compose with `LocalContext.current`
:::

And the shared KMP config:

```kotlin
// Common config
fun koinSharedConfiguration() : KoinAppDeclaration = {
    modules(appModule)
}
```

:::info
The `modules()` function load the given list of modules
:::


## Injecting in iOS

> All the iOS app is located in `iosApp` folder

The `UserPresenter` component will be created, resolving the `UserRepository` instance with it. To get it into our `ContentView`, we need to create a function to retrieve Koin dependencies for iOS: 

```kotlin
// Koin.kt

fun getUserPresenter() : UserPresenter = KoinPlatform.getKoin().get()
```

That's it, you can just call `KoinKt.getUserPresenter().sayHello()` function from iOS part. 

```swift
import Shared

struct ContentView: View {

    // ...
    let greet = KoinKt.getUserPresenter().sayHello(name: "Koin")
}
```

We need to start Koin with our iOS application. In the Kotlin shared code, we can use the shared configuration with `initKoin()` function. 
Finally in the iOS main entry, we can call the `KoinAppKt.doInitKoin()` function that is calling our helper function above.

```swift
@main
struct iOSApp: App {
    
    init() {
        KoinAppKt.doInitKoin()
    }

    //...
}
```
