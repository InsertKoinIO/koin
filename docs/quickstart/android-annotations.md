---
title: Android & Annotations
---

> This tutorial lets you write an Android application and use Koin dependency injection to retrieve your components.
> You need around __10 min__ to do the tutorial.

:::note
update - 2024-10-21
:::

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/android-annotations)
:::

## Gradle Setup

Let's configure the KSP Plugin like this, and the following dependencies:

```groovy
plugins {
    alias(libs.plugins.ksp)
}

dependencies {
    // ...

    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp)
}

// Compile time check
ksp {
    arg("KOIN_CONFIG_CHECK","true")
}
```

:::note
See `libs.versions.toml` for current versions
:::

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

Let's declare a `AppModule` module class like below. 

```kotlin
@Module
@ComponentScan("org.koin.sample")
class AppModule
```

* We use the `@Module` to declare our class as Koin module
* The `@ComponentScan("org.koin.sample")` allow to scan any Koin definition in `"org.koin.sample"`package

Let's simply add `@Single` on `UserRepositoryImpl` class to declare it as singleton:

```kotlin
@Single
class UserRepositoryImpl : UserRepository {
    // ...
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

We declare `UserPresenter` in our Koin module. We declare it as a `factory` definition with the `@Factory` annotation, to not keep any instance in memory (avoid any leak with Android lifecycle):

```kotlin
@Factory
class UserPresenter(private val repository: UserRepository) {
    // ...
}
```

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
// generated
import org.koin.ksp.generated.*

class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        
        startKoin{
            androidLogger()
            androidContext(this@MainApplication)
            modules(AppModule().module)
        }
    }
}
```

The Koin module is generated from `AppModule` with the `.module` extension: Just use the `AppModule().module` expression to get the Koin module from the annotations.

:::info
The `import org.koin.ksp.generated.*` import is required to allow to use generated Koin module content
:::

## Displaying User with ViewModel

Let's write a ViewModel component to display a user:

```kotlin
@KoinViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun sayHello(name : String) : String{
        val foundUser = repository.findUser(name)
        return foundUser?.let { "Hello '$it' from $this" } ?: "User '$name' not found!"
    }
}
```

> UserRepository is referenced in UserViewModel`s constructor

The `UserViewModel` is tagged with `@KoinViewModel` annotation to declare the Koin ViewModel definition, to not keep any instance in memory (avoid any leak with Android lifecycle).


## Injecting ViewModel in Android

The `UserViewModel` component will be created, resolving the `UserRepository` instance with it. To get it into our Activity, let's inject it with the `by viewModel()` delegate function: 

```kotlin
class MainActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        //...
    }
}
```

## Compile Time Checks

Koin Annotations allows to check your Koin configuration at compile time. This is available by jusing the following Gradle option:

```groovy
ksp {
    arg("KOIN_CONFIG_CHECK","true")
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

The `androidVerify()` function allow to verify the given Koin modules:

```kotlin
class CheckModulesTest : KoinTest {

    @Test
    fun checkAllModules() {

        AppModule().module.androidVerify()
    }
}
```

With just a JUnit test, you can ensure your definitions configuration are not missing anything!
