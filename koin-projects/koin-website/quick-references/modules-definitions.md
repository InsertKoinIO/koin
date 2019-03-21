---
layout: docs
title: Modules & definitions
description: How to write your definitions and modules in Koin
group: quick-references
toc: true
---

Writing definitions in Koin is done via Kotlin functions, that describe ishow is built your instance. Once you have configured your Koin application, let's write some modules and definitions.

## Writing module & definitions

Given some classes that we need to inject:

{% highlight kotlin %}
class DataRepository()
interface Presenter
class MyPresenter(val repository : Repository) : Presenter
class HttpClient(val url : String)
{% endhighlight %}

Here how we could define some components:

{% highlight kotlin %}
// declare a module
val myModule = module {

    // Define a singleton for type  DataRepository
    single { DataRepository() }

    // Define a factory (create a new instance each time) for type Presenter (infered parameter in <>) 
    // Resolve constructor dependency with get()
    factory<Presenter> { MyPresenter(get()) }

    // Define a factory (create a new instance each time) for type MyPresenter and Presenter 
    // Resolve constructor dependency with get()
    factory { MyPresenter(get()) } bind Presenter::class
    
    // Define a singleton of type HttpClient
    // inject property "server_url" from Koin properties
    single { HttpClient(getProperty("server_url")) }
}
{% endhighlight %}

## Qualifiers

You can give a qualifier to a component. This qualifier can be a string or a type, and is setup with the `named()` function:

{% highlight kotlin %}
val myModule = module {

    // Define a singleton for type  DataRepository
    single { DataRepository() }

    // Mock
    single(named("mock")) { MockDataRepository() }
}
{% endhighlight %}

## Combining several modules

There is no concept of import in Koin. Just combine several complementary modules definitions. 

Let's dispatch definitions in 2 modules:

{% highlight kotlin %}
val module1 = module {

    // Define a singleton for type  DataRepository
    single { DataRepository() }
}
{% endhighlight %}

{% highlight kotlin %}
val module2 = module {

    // Define a factory for type Presenter (create a new instance each time)
    // Resolve constructor dependency with get()
    factory<Presenter> { MyPresenter(get()) }
}
{% endhighlight %}

We just need to list them for Koin:

{% highlight kotlin %}
startKoin {
    modules(module1,module2)
}
{% endhighlight %}

### Module Keywords recap

A quick recap of the Koin DSL keywords:

* `module { }` - create a Koin Module or a submodule (inside a module)
* `factory { }` - provide a *factory* bean definition
* `single { }` - provide a bean definition
* `get()` - resolve a component dependency
* `named()` - define a qualifier with type or string
* `bind` - additional Kotlin type binding for given bean definition
* `binds` - list of additional Kotlin types binding for given bean definition
* `getProperty()` - resolve a Koin property

## Beyond Koin DSL

Below are some further readings:

