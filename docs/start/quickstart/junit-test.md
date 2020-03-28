
# Getting Started with JUnit Tests {docsify-ignore-all}

> This tutorial lets you test a Kotlin application and use Koin inject and retrieve your components.

## Get the code

> Checkout the project directly on Github or download the zip file <br>
> 🚀 Go to [Github](https://github.com/InsertKoinIO/getting-started-koin-core) or [download Zip](https://github.com/InsertKoinIO/getting-started-koin-core/archive/master.zip)

## Gradle Setup

First, add the Koin dependency like below:

```groovy
// Add Jcenter to your repositories if needed
repositories {
    jcenter()
}
dependencies {
    // Koin testing tools
    testcompile 'org.koin:koin-test:$koin_version'
}
```

## Declared dependencies

We reuse the `koin-core` getting-started project, to use the koin module:

```kotlin
val helloModule = module {
    single { HelloMessageData() }
    single { HelloServiceImpl(get()) as HelloService }
}
```

## Writing our first Test

To make our first test, let's write a simple Junit test file and extend it with `KoinTest`. We will be able then, to use `by inject()` operators.

```kotlin
class HelloAppTest : KoinTest {

    val model by inject<HelloMessageData>()
    val service by inject<HelloService>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(helloModule)
    }

    @Test
    fun `unit test`() {
        val helloApp = HelloApplication()
        helloApp.sayHello()

        assertEquals(service, helloApp.helloService)
        assertEquals("Hey, ${model.message}", service.hello())
    }
}
```

> We use the Koin KoinTestRule rule to start/stop our Koin context

You can even make Mocks directly into MyPresenter, or test MyRepository. Those components doesn't have any link with Koin API.

```kotlin
class HelloMockTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger(Level.DEBUG)
        modules(helloModule)
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun `mock test`() {
        val service = declareMock<HelloService> {
            given(hello()).willReturn("Hello Mock")
        }

        HelloApplication().sayHello()

        Mockito.verify(service,times(1)).hello()
    }
}
```

## Verifying modules

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
        modules(helloModule)
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