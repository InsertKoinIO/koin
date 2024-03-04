---
title: Kotlin Multiplatform Mobile
---

> This tutorial lets you write an Android application and use Koin dependency injection to retrieve your components.
> You need around __10/15 min__ to do the tutorial.

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/kmm)
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
    single<UserRepository> { UserRepositoryImpl() }
}
```

## The Shared Presenter

Let's write a presenter component to display a user:

```kotlin
class KMPUserPresenter(private val repository: UserRepository) {

    fun sayHello() : String {
        val name = DefaultData.DEFAULT_USER.name
        val foundUser = repository.findUser(name)
        return foundUser?.let { "Hello '$it' from $this" } ?: "User '$name' not found!"
    }
}
```

> UserRepository is referenced in UserPresenter`s constructor

We declare `UserPresenter` in our Koin module. We declare it as a `factory` definition, to not keep any instance in memory and let the native system hold it:

```kotlin
fun appModule() = module {
    single<UserRepository> { UserRepositoryImpl() }
    factory { KMPUserPresenter(get()) }
}
```

:::note
The Koin module is available as function to run (`appModule()` here), to be easily runned from iOS side, with `initKoin()` function. 
:::

## Injecting Dependencies in Android

> All the Android app is located in `androidApp` Gradle project

The `KMPUserPresenter` component will be created, resolving the `UserRepository` instance with it. To get it into our Activity, let's inject it with the `by inject()` delegate function: 

```kotlin
class MainActivity : AppCompatActivity() {

    private val presenter: UserPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        //...
    }
}
```

That's it, your app is ready.

:::info
The `by inject()` function allows us to retrieve Koin instances, in Android components runtime (Activity, fragment, Service...)
:::

We need to start Koin with our Android application. Just call the `startKoin()` function in the application's main entry point, our `MainApplication` class:

```kotlin
class MainApplication : Application() {

    private val userRepository : UserRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules(appModule() + androidModule)
        }

        userRepository.addUsers(DefaultData.DEFAULT_USERS)
    }
}
```

:::info
The `modules()` function in `startKoin` load the given list of modules
:::


## Injecting Dependencies in iOS

> All the iOS app is located in `iosApp` folder

The `KMPUserPresenter` component will be created, resolving the `UserRepository` instance with it. To get it into our `ContentView`, we need to create a Helper class to boostrap Koin dependencies: 

```kotlin
class KMPUserPresenterHelper : KoinComponent {

    private val userPresenter : KMPUserPresenter by inject()

    fun sayHello(): String = userPresenter.sayHello()
}
```

That's it, you can just call `sayHello()` function from iOS part. 

```swift
import shared

struct ContentView: View {
    let helloText = KMPUserPresenterHelper().sayHello()

	var body: some View {
		Text(helloText)
	}
}
```

We need to start Koin with our iOS application. In the Kotlin shared code, we have a function to let us configure Koin (and setup default data):

```kotlin
// in HelperKt.kt

fun initKoin() {
    // start Koin
    val koinApp = startKoin {
        modules(appModule())
    }.koin
    
    // load default users
    koinApp.get<UserRepository>().addUsers(DefaultData.DEFAULT_USERS)
}
```

Finally in the iOS main entry, we can call the `HelperKt.doInitKoin()` function that is calling our helper function above.

```swift
@main
struct iOSApp: App {
    
    init() {
        HelperKt.doInitKoin()
    }
    
    //...
}
```


