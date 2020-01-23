
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
    testcompile 'org.koin:koin-test:$koinVersion'
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
class HelloAppTest : KoinTest() {

    val model by inject<HelloMessageData>()
    val service by inject<HelloService>()

    @Before
    fun before() {
        startKoin {
            modules(helloModule) 
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun testKoinComponents() {
        val helloApp = HelloApplication()
        helloApp.sayHello()

        assertEquals(service, helloApp.helloService)
        assertEquals("Hey, ${model.message}", service.hello())
    }
}
```

<div class="alert alert-warning" role="alert">
  For each test, we start <b>startKoin()</b> and stop Koin context <b>stopKoin()</b>.
</div>

You can even make Mocks directly into MyPresenter, or test MyRepository. Those components doesn't have any link with Koin API.

```kotlin
class HelloMockTest : AutoCloseKoinTest() {

    val service: HelloService by inject()

    @Before
    fun before() {
        startKoin{
            modules(helloModule)
        }
        declareMock<HelloService>()
    }

    @Test
    fun tesKoinComponents() {
        HelloApplication().sayHello()

        Mockito.verify(service).hello()
    }
}
```


## What's next?

* Check the [DSL quick references]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-dsl/)
* Check the [Core features quick references]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-core/)
* Read the [full documentation]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html)
