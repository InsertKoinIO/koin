---
title: Android
---

> This tutorial lets you write an Android application and use Koin dependency injection to retrieve your components.
> You need around __10 min__ to do the tutorial.

:::note
update - 2024-10-21
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/android)
:::

## Gradle Setup

Add the Koin Android dependency like below:

```groovy
dependencies {

    // Koin for Android
    implementation("io.insert-koin:koin-android:$koin_version")
}
```

## Application Overview

The idea of the application is to manage a list of users, and display it in our `MainActivity` class with a Presenter or a ViewModel:

> Users -> UserRepository -> (Presenter or ViewModel) -> MainActivity

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
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
}
```

## Displaying User with Presenter

Let's write a presenter component to display a user:

```kotlin
class UserPresenter(private val repository: UserRepository) {

    fun sayHello(name : String) : String{
        val foundUser = repository.findUser(name)
        return foundUser?.let { "Hello '$it' from $this" } ?: "User '$name' not found!"
    }
}
```

> UserRepository is referenced in UserPresenter`s constructor

We declare `UserPresenter` in our Koin module. We declare it as a `factoryOf` definition, to not keep any instance in memory (avoid any leak with Android lifecycle):

```kotlin
val appModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    factoryOf(::UserStateHolder)
}
```

> The `get()` function allow to ask Koin to resolve the needed dependency.

## Injecting Dependencies in Android

The `UserPresenter` component will be created, resolving the `UserRepository` instance with it. To get it into our Activity, let's inject it with the `by inject()` delegate function: 

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

## Start Koin

We need to start Koin with our Android application. Just call the `startKoin()` function in the application's main entry point, our `MainApplication` class:

```kotlin
class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        
        startKoin{
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}
```

:::info
The `modules()` function in `startKoin` load the given list of modules
:::

## Koin module: classic or constructor DSL?

Here is the Koin module declaration for our app:

```kotlin
val appModule = module {
    single<HelloRepository> { HelloRepositoryImpl() }
    factory { MyPresenter(get()) }
}
```

We can write it in a more compact way, by using constructors:

```kotlin
val appModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    factoryOf(::UserPresenter)
}
```

## Verifying your App!

We can ensure that our Koin configuration is good before launching our app, by verifying our Koin configuration with a simple JUnit Test.

### Gradle Setup

Add the Koin Android dependency like below:

```groovy
// Add Maven Central to your repositories if needed
repositories {
	mavenCentral()    
}

dependencies {
    
    // Koin for Tests
    testImplementation "io.insert-koin:koin-test-junit4:$koin_version"
}
```

### Checking your modules

The `verify()` function allow to verify the given Koin modules:

```kotlin
class CheckModulesTest : KoinTest {

    @Test
    fun checkAllModules() {
        appModule.verify()
    }
}
```

With just a JUnit test, you can ensure your definitions configuration are not missing anything!
