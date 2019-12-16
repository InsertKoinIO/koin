# Getting Started with Android application {docsify-ignore-all}

> This tutorial lets you write an Android/Kotlin application and use Koin inject and retrieve your components.

## Get the code

Checkout the project directly on Github or download the zip file

> 🚀 Go to [Github](https://github.com/InsertKoinIO/getting-started-koin-android) or [download Zip](https://github.com/InsertKoinIO/getting-started-koin-android/archive/master.zip)

## Gradle Setup

Add the Koin Android dependency like below:

```groovy
// Add Jcenter to your repositories if needed
repositories {
    jcenter()    
}
dependencies {
    // Koin for Android
    compile 'org.koin:koin-android:$koin_version
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

Let's create a presenter class, for consuming this data:

```kotlin
class MySimplePresenter(val repo: HelloRepository) {

    fun sayHello() = "${repo.giveHello()} from $this"
}
```

## Writing the Koin module

Use the `module` function to declare a module. Let's declare our first component:

```kotlin
val appModule = module {

    // single instance of HelloRepository
    single<HelloRepository> { HelloRepositoryImpl() }

    // Simple Presenter Factory
    factory { MySimplePresenter(get()) }
}
```

?> we declare our MySimplePresenter class as `factory` to have a create a new instance each time our Activity will need one.

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

The `MySimplePresenter` component will be created with `HelloRepository` instance. To get it into our Activity, let's inject it with the `by inject()` delegate injector: 

```kotlin
class MySimpleActivity : AppCompatActivity() {

    // Lazy injected MySimplePresenter
    val firstPresenter: MySimplePresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        //...
    }
}
```

?> The `by inject()` function allows us to retrieve Koin instances, in Android components runtime (Activity, fragment, Service...)

?> The `get()` function is here to retrieve directly an instance (non lazy)

