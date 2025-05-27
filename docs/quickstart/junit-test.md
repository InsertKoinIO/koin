---
title: JUnit Tests
---

> This tutorial lets you test a Kotlin application and use Koin inject and retrieve your components.

## Get the code

:::info
[The source code is available at on Github](https://github.com/InsertKoinIO/koin-getting-started/tree/main/kotlin)
:::

## Gradle Setup

First, add the Koin dependency like below:

```groovy
dependencies {
    // Koin testing tools
    testImplementation "io.insert-koin:koin-test:$koin_version"
    // Needed JUnit version
    testImplementation "io.insert-koin:koin-test-junit4:$koin_version"
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
