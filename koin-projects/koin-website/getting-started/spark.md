---
layout: docs
title: Getting started with SparkJava
description: Let's start with a Spark web application & Koin
group: getting-started
toc: true
---

## About

SparkJava has now its [Kotlin declination](http://sparkjava.com/news#spark-kotlin-released), and continue to assert its ambition for developing webapps in a smart & simple way.

Let's go with the following Spark components to chain : a Controller, a Service and a Repository. All of those components assembled behind Spark with Koin.

{% highlight kotlin %}
Controller (http) -> Service (business) -> Repository (data)
{% endhighlight %}

- a Controller to handle http route and return result from the service
- a Service to *handle business* and take data from repository
- a Repository to provide data

Let's go 👍

## Get the code

<div class="container">
  <div class="row">
    <div class="col-8">
      Checkout the project directly on Github or download the zip file
    </div>
    <div class="col">
      <a href="https://github.com/InsertKoinIO/getting-started-koin-spark" class="btn btn-outline-primary mb-3 mb-md-0 mr-md-3">Go to Github</a>
      <a href="https://github.com/InsertKoinIO/getting-started-koin-spark/archive/master.zip" class="btn btn-outline-info mb-3 mb-md-0 mr-md-3">Download Zip</a>
    </div>
  </div>
</div>

## Gradle Setup

First, add the Koin dependency like below:

{% highlight kotlin %}
// Add Jcenter to your repositories if needed
repositories {
    jcenter()
}
dependencies {
    // Koin for Kotlin apps
    compile 'org.koin:koin-spark:{{ site.current_version }}'
}
{% endhighlight %}

## Service & Repository

Let's write our Service, a component that will ask Repository for data:

{% highlight kotlin %}
interface HelloService {
    fun sayHello(): String
}

class HelloServiceImpl(val helloRepository: HelloRepository) : HelloService {
    override fun sayHello() = "Hello ${helloRepository.getHello()} !"
}
{% endhighlight %}

and our Repository, which provide data:

{% highlight kotlin %}
interface HelloRepository {
    fun getHello(): String
}

class HelloRepositoryImpl : HelloRepository {
    override fun getHello(): String = "Spark & Koin"
}
{% endhighlight %}

Finally, we need an HTTP Controller to create the HTTP Route

{% highlight kotlin %}
class HelloController(val service: HelloService) : SparkController{
    init {
        get("/hello") {
            service.sayHello()
        }
    }
}
{% endhighlight %}

## Declare your dependencies

Let's assemble our components with a Koin module:

{% highlight kotlin %}
val helloAppModule = module {
    single<HelloService> { HelloServiceImpl(get()) } // get() Will resolve HelloRepository
    single<HelloRepository> { HelloRepositoryImpl() }
    controller { HelloController(get()) } // get() Will resolve HelloService
}
{% endhighlight %}

## Start and Inject

Finally, let's start Koin:

{% highlight kotlin %}
fun main(vararg args: String) {
    // Start Spark server & Koin with given modules
    start(modules = listOf(helloAppModule))
}
{% endhighlight %}

That's it! You're ready to go.

## What's Next?

You have finished this starting tutorial. Below are some topics for further reading:

* Check the [DSL quick references]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-dsl/)
* Check the [Core features quick references]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-core/)
* Read the [full documentation]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html)

Also other getting started project:

* [Getting started Android]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/android/)
* [Getting started Ktor]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/ktor/)
* [Getting started Core]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/kotlin/)

