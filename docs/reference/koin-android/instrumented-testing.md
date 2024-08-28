---
title: Android Instrumented Testing
---

## Override production modules in a custom Application class

Unlike [unit tests](/docs/reference/koin-test/testing.md), where you effectively call start Koin in each test class (i.e. `startKoin` or `KoinTestExtension`), in Instrumented tests Koin is started by your `Application` class. 

For overriding production Koin modules, `loadModules` and `unloadModules` are often unsafe because the changes are not applied immediately. Instead, the recommended approach is to add a `module` of your overrides to `modules` used by `startKoin` in the `Application` class.
If you want to keep the class that extends `Application` of your application untouched, you can create another one inside the `AndroidTest` package like:
```kotlin
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(productionModule, instrumentedTestModule)
        }
    }
}
```
In order to use this custom `Application` in yours Instrumentation tests you may need to create a custom [AndroidJUnitRunner](https://developer.android.com/training/testing/instrumented-tests/androidx-test-libraries/runner) like:
```kotlin
class InstrumentationTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(classLoader, TestApplication::class.java.name, context)
    }
}
```
And then register it inside your gradle file with:
```groovy
testInstrumentationRunner "com.example.myapplication.InstrumentationTestRunner"
```

## Override production modules with a test rule

If you want more flexibility, you still have to create the custom `AndroidJUnitRunner` but instead of having `startKoin { ... }` inside the custom application, you can put it inside a custom test rule like:
```kotlin
class KoinTestRule(
    private val modules: List<Module>
) : TestWatcher() {
    override fun starting(description: Description) {

        if (getKoinApplicationOrNull() == null) {
            startKoin {
                androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
                modules(modules)
            }
        } else {
            loadKoinModules(modules)
        }
    }

    override fun finished(description: Description) {
        unloadKoinModules(modules)
    }
}
```
In this way we can potentially override the definitions directly from our test classes, like:
```kotlin
private val instrumentedTestModule = module {
    factory<Something> { FakeSomething() }
}

@get:Rule
val koinTestRule = KoinTestRule(
    modules = listOf(productionModule, instrumentedTestModule)
)
```
