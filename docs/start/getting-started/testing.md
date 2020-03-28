

The `koin-test` project brings you small but powerful tools to test your Koin application.

## Getting your components

Just tag your test class with `KoinTest`, and you will be able to unlock `KoinComponent` & testing features:

* `by inject()` - lazy inject an instance
* `get()` - retrieve an instance

Given the definitions below:

```kotlin
val appModule = module {
    single { ComponentA() }
    //...
}
```

We can write the test below:

```kotlin
class MyTest : KoinTest {

    // Lazy inject property
    val componentA : ComponentA by inject()

    // use it in your tests :)
    @Test
    fun `make a test with Koin`() {
        startKoin { modules(appModule) }

        // use componentA here!
    }
}
```

you can use the `KoinTestRule` JUnit rule to statr/stop  your Koin context:

```kotlin
class MyTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appModule)
    }

    // Lazy inject property
    val componentA : ComponentA by inject()

    // use it in your tests :)
    @Test
    fun `make a test with Koin`() {
        // use componentA here!
    }
}
```

## Checking your modules

We can use the Koin gradle plugin to let us run our module checks:

```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "org.koin:koin-gradle-plugin:$koin_version"
    }
}

apply plugin: 'koin'
```

Let's write our check test as follow:
- using a JUnit `CheckModuleTest` category
- test modules with `checkModules { }` API

```kotlin
@Category(CheckModuleTest::class)
class ModuleCheckTest : AutoCloseKoinTest() {

    @Test
    fun checkModules() = checkModules {
        modules(appModule)
    }
}
```

Let's check our modules via Gradle command:

```
./gradlew checkModules
```

or 

```
./gradlew checkModules --continuous
```

## Mocking on the fly

Once you have tagged your class with `KoinTest` interface, you can use the `declareMock` function to declare mocks & behavior on the fly:

```kotlin
class MyTest : KoinTest {
    
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(appModule)
    }
    
    // required to make your Mock via Koin
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    val componentA : ComponentA by inject()

    @Test
    fun `declareMock with KoinTest`() {
        declareMock<ComponentA> {
            // do your given behavior here
            given(this.sayHello()).willReturn("Hello mock")
        }
    }
}
```

## Starting & stopping for tests

Take attention to stop your koin instance (if you use `startKoin` in your tests) between every test. Else be sure to use `koinApplication`, for local koin instances or `stopKoin()` to stop the current global instance.

