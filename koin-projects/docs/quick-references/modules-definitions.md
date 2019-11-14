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

## Additional types (binding secondary types)

In the module DSL, for a definition, you can give some extra type to bind, with the `bind` operator (`binds` for list of KClass):

{% highlight kotlin %}
module {
    single { Component1() } bind ComponentInterface1::class
}
{% endhighlight %}

Then you can request your instance with `get<Component1>()` or `get<ComponentInterface1>()`.

You can bind multiple definitions with the same type:

{% highlight kotlin %}
module {
    single { Component1() } bind ComponentInterface1::class
    single { Component2() } bind ComponentInterface1::class
}
{% endhighlight %}

But here you won't be able to request an instance with `get<Simple.ComponentInterface1>()`. You will have to use `koin.bind<Component1,ComponentInterface1>()` to retrieve an instance of `ComponentInterface1`, with the `Component1` implementation.

Note that you cal also look for all components binding a given type: `getAll<ComponentInterface1>()` will request all instances binding `ComponentInterface1` type.


## Combining several modules

There is no concept of import in Koin. Just combine several complementary modules definitions. 

Let's dispatch definitions in 2 modules:

{% highlight kotlin %}
val module1 = module {
    single { DataRepository() }
}
val module2 = module {
    factory<Presenter> { MyPresenter(get()) }
}
{% endhighlight %}

We just need to list them for Koin:

{% highlight kotlin %}
startKoin {
    modules(module1,module2)
}
{% endhighlight %}

## Loading modules after start

After Koin has been started with `startKoin { }` function, it is possible to load extra definitions modules with the following function: `loadKoinModules(modules...)`

## Dropping definitions & modules - definitions unload 

Once a modules has been loaded into Koin, we can unload it and then drop definitions and instances, related to those definitions. for this we use the `unloadKoinModules(modules...)` 

{% highlight kotlin %}
val module = module {
    single { (id: Int) -> Simple.MySingle(id) }
}
startKoin {
    printLogger(Level.DEBUG)
    modules(module)
}

get<Simple.MySingle> { parametersOf(42) } -> id is 42

// unload definitions for given module
unloadKoinModules(module)
// load definitions for given module
loadKoinModules(module)

get<Simple.MySingle> { parametersOf(24) } -> id is 24
{% endhighlight %}

## Declare instance on the fly

One of the last backport feature from Koin 1.0 is the ability to declare an instance on the fly. This is now available on Koin.declare() or Scope.declare()

{% highlight kotlin %}
val koin = koinApplication {
    // no def
    modules()
}.koin

// Create an instance
val a = Simple.ComponentA()

// declare it
koin.declare(a)

// retrieve it
assertEquals(a, koin.get<Simple.ComponentA>())
{% endhighlight %}


You can also use a qualifier or secondary types to help create your definition:

- `koin.declare(myInstance, named("qualifier"))`
- `koin.declare(myInstance, secondaryTypes = listOf())`

<br/>

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

