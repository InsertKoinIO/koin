---
title: Get the Android Context
---

## Getting Android context inside a Module

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

