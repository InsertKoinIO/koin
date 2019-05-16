---
layout: docs
title: Koin Components
description: declare, start & inject
group: quick-references
toc: true
---

Sometimes you can't declare only components via Koin. Dependening on your runtime technology, you can need to retrieve instances from Koin in a class
that is not created with Koin (i.e: Android)

## Using the KoinComponent interface

Tag your class with the `KoinComponent` interface to unlock Koin injection features:

* `by inject()` - lazy inject an instance
* `get()` - retrieve an instance
* `getProperty()` - get a Koin property

We can inject the module above into class properties:

{% highlight kotlin %}
// Tag class with KoinComponent
class HelloApp : KoinComponent {

    // lazy inject dependency
    val helloService: HelloServiceImpl by inject()

    fun sayHello(){
        helloService.sayHello()
    }
}
{% endhighlight %}

And we just need to start Koin and run our class:

{% highlight kotlin %}
// a module with our declared Koin dependencies 
val helloModule = module {
    single { HelloServiceImpl() }
}

fun main(vararg args: String) {

    // Start Koin
    startKoin {
        modules(helloModule)
    }

    // Run our Koin component
    HelloApp().sayHello()
}
{% endhighlight %}

## Bootstrap and runtime extensions

`KoinComponent` interface is also used to help you boostrap an application from outside of Koin. Also, you can bring `KoinComponent` feature by extension functions directly on some tagret classes (i.e: Activity, Fragment have KoinComponent feature in Android). 


## Bridge with Koin instance & current Scope

The `KoinComponent` interface brings the following:

{% highlight kotlin %}
interface KoinComponent {

    /**
     * Get the associated Koin instance
     */
    fun getKoin(): Koin = GlobalContext.get().koin
}
{% endhighlight %}

It opens the following possibilties:

* You can then redefine then `getKoin()` function to redirect to a local custom Koin instance

## More about core Koin Components

Below are some further readings:


