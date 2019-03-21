---
layout: docs
title: Getting started with JUnit Tests
description: Let's test our Koin injected application
group: getting-started
toc: true
---

## About

This tutorial lets you test a Kotlin application and use Koin inject and retrieve your components.

## Get the code

<div class="container">
  <div class="row">
    <div class="col-8">
      Checkout the project directly on Github or download the zip file
    </div>
    <div class="col">
      <a href="https://github.com/InsertKoinIO/getting-started-koin-core" class="btn btn-outline-primary mb-3 mb-md-0 mr-md-3">Go to Github</a>
      <a href="https://github.com/InsertKoinIO/getting-started-koin-core/archive/master.zip" class="btn btn-outline-info mb-3 mb-md-0 mr-md-3">Download Zip</a>
    </div>
  </div>
</div>

## Gradle Setup

First, add the Koin dependency like below:

{% highlight gradle %}
// Add Jcenter to your repositories if needed
repositories {
    jcenter()
}
dependencies {
    // Koin testing tools
    testcompile 'org.koin:koin-test:{{ site.current_version }}'
}
{% endhighlight %}

## Declared dependencies

We reuse the `koin-core` getting-started project, to use the koin module:

{% highlight kotlin %}
val helloModule = module {
    single { HelloMessageData() }
    single { HelloServiceImpl(get()) as HelloService }
}
{% endhighlight %}

## Writing our first Test

To make our first test, let's write a simple Junit test file and extend it with `KoinTest`. We will be able then, to use `by inject()` operators.

{% highlight kotlin %}
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
        closeKoin()
    }

    @Test
    fun tesKoinComponents() {
        val helloApp = HelloApplication()
        helloApp.sayHello()

        assertEquals(service, helloApp.helloService)
        assertEquals("Hey, ${model.message}", service.hello())
    }
}
{% endhighlight %}

<div class="alert alert-warning" role="alert">
  For each test, we start <b>startKoin()</b> and close Koin context <b>closeKoin()</b>.
</div>

You can even make Mocks directly into MyPresenter, or test MyRepository. Those components doesn't have any link with Koin API.

{% highlight kotlin %}
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
{% endhighlight %}


## What's next?

* Check the [DSL quick references]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-dsl/)
* Check the [Core features quick references]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-core/)
* Read the [full documentation]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html)