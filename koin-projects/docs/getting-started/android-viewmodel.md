# Getting Started with Android ViewModel application {docsify-ignore-all}

> This tutorial lets you write an Android/Kotlin application and use Koin inject and retrieve your ViewModel components.

## Get the code

Checkout the project directly on Github or download the zip file

[Github](https://github.com/InsertKoinIO/getting-started-koin-android)
[Download Zip](https://github.com/InsertKoinIO/getting-started-koin-android/archive/master.zip)

## Gradle Setup

Add the Koin Android dependency like below:

```groovy
// Add Jcenter to your repositories if needed
repositories {
    jcenter()    
}
dependencies {
    // Koin for Android - Scope feature
    // include koin-android-scope & koin-android
    compile 'org.koin:koin-android-viewmodel:$koin_version'
}
```

## Our components

Let's create a HelloRepository to provide some data:

```kotlin
interface HelloRepository {
    fun giveHello(): String
}

class HelloRepositoryImpl() : HelloRepository {
    override fun giveHello() = "Hello Koin"
}
```

Let's create a ViewModel class, for consuming this data:

```kotlin
class MyViewModel(val repo : HelloRepository) : ViewModel() {

    fun sayHello() = "${repo.giveHello()} from $this"
}
```

## Writing the Koin module

Use the `module` function to declare a module. Let's declare our first component:

```kotlin
val appModule = module {

    // single instance of HelloRepository
    single<HelloRepository> { HelloRepositoryImpl() }

    // MyViewModel ViewModel
    viewModel { MyViewModel(get()) }
}
```

*Note:* we declare our MyViewModel class as a `viewModel` in a `module`. Koin will give a `MyViewModel` to the lifecycle ViewModelFactory and help bind it to the current component.

## Start Koin

Now that we have a module, let's start it with Koin. Open your application class, or make one (don't forget to declare it in your manifest.xml). Just call the `startKoin()` function:

```kotlin
class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
```

## Injecting dependencies

The `MyViewModel` component will be created with `HelloRepository` instance. To get it into our Activity, let's inject it with the `by viewModel()` delegate injector: 

```kotlin
class MyViewModelActivity : AppCompatActivity() {
    
    // Lazy Inject ViewModel
    val myViewModel: MyViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

        //...
    }
}
```

<div class="alert alert-primary" role="alert">
    The <b>by viewModel()</b> function allows us to retrieve a ViewModel instance from Koin, linked to the Android ViewModelFactory.
</div>

<div class="alert alert-secondary" role="alert">
    The <b>getViewModel()</b> function is here to retrieve directly an instance (non lazy)
</div>


## What's Next?

You have finished this starting tutorial. Below are some topics for further reading:

* [Android Quick References]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/)

Also other Android getting started project:

* [Getting started Android & Scope feature]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/android-scope/)
* [Getting started Android]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/android/)
