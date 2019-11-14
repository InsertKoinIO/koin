---
layout: docs
title: Getting started with Kotlin & Koin
description: Let's start with a standalone Kotlin app
group: getting-started
toc: true
---

## About

This tutorial lets you write a Kotlin application and use Koin inject and retrieve your components.

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

## Setup

First, check that the `koin-core` dependency is added like below:

{% highlight gradle %}
// Add Jcenter to your repositories if needed
repositories {
    jcenter()    
}
dependencies {
    // Koin for Kotlin apps
    compile 'org.koin:koin-core:{{ site.current_version }}'
    // Testing
    testCompile 'org.koin:koin-test:{{ site.current_version }}'
}
{% endhighlight %}

## The application

In our small app we need to have 2 components:

* HelloMessageData - hold data
* HelloService - use and display data from HelloMessageData
* HelloApplication - retrieve and use HelloService

### Data holder

Let's create a `HelloMessageData` data class to hold our data:

{% highlight kotlin %}
/**
 * A class to hold our message data
 */
data class HelloMessageData(val message : String = "Hello Koin!")
{% endhighlight %}

### Service

Let's create a service to display our data from `HelloMessageData`. Let's write `HelloServiceImpl` class and its interface `HelloService`:

{% highlight kotlin %}
/**
 * Hello Service - interface
 */
interface HelloService {
    fun hello(): String
}


/**
 * Hello Service Impl
 * Will use HelloMessageData data
 */
class HelloServiceImpl(private val helloMessageData: HelloMessageData) : HelloService {

    override fun hello() = "Hey, ${helloMessageData.message}"
}
{% endhighlight %}


## The application class

To run our `HelloService` component, we need to create a runtime component. Let's write a `HelloApplication` class and tag it with `KoinComponent` interface. This will later allows us to use the `by inject()` functions to retrieve our component:

{% highlight kotlin %}
/**
 * HelloApplication - Application Class
 * use HelloService
 */
class HelloApplication : KoinComponent {

    // Inject HelloService
    val helloService by inject<HelloService>()

    // display our data
    fun sayHello() = println(helloService.hello())
}
{% endhighlight %}

## Declaring dependencies

Now, let's assemble `HelloMessageData` with `HelloService`, with a Koin module:

{% highlight kotlin %}
val helloModule = module {

    single { HelloMessageData() }

    single { HelloServiceImpl(get()) as HelloService }
}
{% endhighlight %}

We declare each component as `single`, as singleton instances.

* `single { HelloMessageData() }` : declare a singleton of `HelloMessageData` instance
* `single { HelloServiceImpl(get()) as HelloService }` : Build `HelloServiceImpl` with injected instance of `HelloMessageData`,  declared a singleton of `HelloService`.

## That's it!

Just start our app from a `main` function:

{% highlight kotlin %}
fun main(vararg args: String) {

    startKoin {
        // use Koin logger
        printLogger()
        // declare modules
        modules(helloModule)
    }

    HelloApplication().sayHello()
}

{% endhighlight %}

## What's next?

* Check the [Koin quick references]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/)
* Read the [full documentation]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html)

Also other getting started project:

* [Getting started Android]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/android/)
* [Getting started Ktor]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/ktor/)
* [Getting started Spark]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/spark/)
