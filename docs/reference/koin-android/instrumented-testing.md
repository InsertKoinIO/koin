---
title: Android Instrumented Testing
---

## Override production modules

Unlike [unit tests](../koin-test/testing.md), where you effectively call start Koin in each test class (i.e. `startKoin` or `KoinTestExtension`), in Instrumented tests Koin is started by your `Application` class. 

For overriding production Koin modules, `loadModules` and `unloadModules` are often unsafe because the changes are not applied immediately. Instead,the recommended approach is to add a `module` of your overrides to `modules` used by `startKoin` in the application's `Application` class.
```
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(productionModule, instrumentedTestModule)
        }
    }
}
```

In order to use this custom `Application` for yours Instrumentation tests you may need to create a custom [AndroidJUnitRunner](https://developer.android.com/training/testing/instrumented-tests/androidx-test-libraries/runner) like:
```
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
```
testInstrumentationRunner "com.example.myapplication.InstrumentationTestRunner"
```

If you want more flexibility you can remove `startKoin { ... }` from the custom application and create a test rule like:
```
class KoinTestRule(
    private val modules: List<Module>
) : TestWatcher() {
    override fun starting(description: Description) {
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
            modules(modules)
        }
    }

    override fun finished(description: Description) {
        stopKoin()
    }
}
```
In this way we can potentially override the definitions directly from our test classes, like:
```
private val instrumentedTestModule = module {
    factory<Something> { FakeSomething() }
}

@get:Rule
val koinTestRule = KoinTestRule(
    modules = listOf(productionModule, instrumentedTestModule)
)
```
