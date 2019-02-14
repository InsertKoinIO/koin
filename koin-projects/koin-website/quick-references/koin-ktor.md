---
layout: docs
title: koin-ktor
description: Koin features for Ktor framework
group: quick-references
toc: true
---

## Install Koin & inject

To start Koin container, use the `installKoin()` starter function:


{% highlight kotlin %}
fun Application.main() {
    // Install Ktor features
    install(DefaultHeaders)
    install(CallLogging)
    
    // Install Koin
    installKoin(listOf(helloAppModule), logger = SLF4JLogger())

    //...
}
{% endhighlight %}

KoinComponent powers are available from `Application` class:


{% highlight kotlin %}
fun Application.main() {
    //...

    // Lazy inject HelloService
    val service by inject<HelloService>()

    // Routing section
    routing {
        get("/hello") {
            call.respondText(service.sayHello())
        }
    }
}
{% endhighlight %}

From `Routing` class:


{% highlight kotlin %}
fun Application.main() {
    //...

    // Lazy inject HelloService
    val service by inject<HelloService>()

    // Routing section
    routing {
        v1()
    }
}

fun Routing.v1() {

    // Lazy inject HelloService from within a Ktor Routing Node
    val service by inject<HelloService>()

    get("/v1/hello") {
        call.respondText("[/v1/hello] " + service.sayHello())
    }
}

{% endhighlight %}


From `Route` class:


{% highlight kotlin %}
fun Application.main() {
    //...

    // Lazy inject HelloService
    val service by inject<HelloService>()

    // Routing section
    routing {
        v1()
    }
}

fun Routing.v1() {
    hello()
}

fun Route.hello() {

    // Lazy inject HelloService from within a Ktor Route
    val service by inject<HelloService>()

    get("/v1/bye") {
        call.respondText("[/v1/bye] " + service.sayHello())
    }
}

{% endhighlight %}