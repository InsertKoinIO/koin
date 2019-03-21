---
layout: docs
title: Koin for Ktor
description: Koin features for Ktor framework
group: quick-references
toc: true
---

### Starting Koin with Ktor

Start Koin from your `Application` with the Ktor extension below:

{% highlight kotlin %}
fun Application.main() {
    // Install Ktor features
    install(Koin) {
        // Use SLF4J Koin Logger at Level.INFO
        slf4jLogger()

        // declare used modules
        modules(helloAppModule)
    }
}
{% endhighlight %}

## Application, Route and Routing are KoinComponents

Easily access `KoinComponent` features from `Application`, `Route` and `Routing` Ktor classes:

{% highlight kotlin %}
fun Routing.v1() {

    // Lazy inject HelloService from within a Ktor Routing Node
    val service by inject<HelloService>()

    get("/v1/hello") {
        call.respondText("[/v1/hello] " + service.sayHello())
    }
}
{% endhighlight %}

## Declare components without function (Experimental feature)

Easily declare your components without any description function:

{% highlight kotlin %}
module(createdAtStart = true) {
    singleBy<HelloService, HelloServiceImpl>()
    single<HelloRepository>()
}
{% endhighlight %}

instead of 

{% highlight kotlin %}
module(createdAtStart = true) {
    singleBy<HelloService> { HelloServiceImpl(get()) }
    single { HelloRepository() } 
}
{% endhighlight %}