---
title: Injecting in Android
---


Once you have declared some modules and you have started Koin, how can you retrieve your instances in your
Android Activity Fragments or Services?

## Ready for Android Classes

`Activity`, `Fragment` & `Service` are extended with the KoinComponents extension. Any `ComponentCallbacks` class is accessible for the Koin extensions.

You gain access for the Kotlin extensions:

* `by inject()` - lazy evaluated instance from Koin container
* `get()` - eager fetch instance from Koin container

We can declare a property as lazy injected:

```kotlin
module {
    // definition of Presenter
    factory { Presenter() }
}
```

```kotlin
class DetailActivity : AppCompatActivity() {

    // Lazy inject Presenter
    override val presenter : Presenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        //...
    }
}
```

Or we can just directly get an instance:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Retrieve a Presenter instance
    val presenter : Presenter = get()
}  
```

:::info
if you class doesn't have extensions, just add KoinComponent interface If you need to `inject()` or `get()` an instance from another class.
:::

## Using the Android Context in a Definition

Once your `Application` class you can use `androidContext` function:

```kotlin
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            //inject Android context
            androidContext(this@MainApplication)
            // ...
        }
        
    }
}
```

In your definitions,  The `androidContext()` & `androidApplication()` functions allows you to get the `Context` instance in a Koin module, to help you simply write expression that requires the `Application` instance.

```kotlin
val appModule = module {

    // create a Presenter instance with injection of R.string.mystring resources from Android
    factory {
        MyPresenter(androidContext().resources.getString(R.string.mystring))
    }
}
```

## Android Scope & Android Context resolution

While you have a scope that is binding the `Context` type, you may need to resolve the `Context` but from different level.

Let's take a config:

```kotlin
class MyPresenter(val context : Context)

startKoin {
  androidContext(context)
  modules(
    module {
      scope<MyActivity> {
        scoped { MyPresenter( <get() ???> ) }
      }
    }
  )
}
```

To resolve the right type in `MyPresenter`, use the following:
- `get()` will resolve closest `Context` definition, here it will be the source scope `MyActivity`
- `androidContext()` will also resolve closest `Context` definition, here it will be the source scope `MyActivity`
- `androidApplication()` will also resolve `Application` definition, here it will be the source scope `context` object defined in Koin setup
