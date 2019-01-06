---
layout: docs
title: Koin DSL
description: All the Koin DSL essentials
group: quick-references
toc: true
---

## The KOIN DSL in 5 minutes

Below a short description on Koin DSL and how to write a module. For more detailed information please refer to the [documentation]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/koin-core/index.html#_koin_dsl).

### Keywords

A quick recap of the Koin DSL keywords:

* `module { }` - create a Koin Module or a submodule (inside a module)
* `factory { }` - provide a *factory* bean definition
* `single { }` - provide a bean definition
* `get()` - resolve a component dependency
* `bind` - additional Kotlin type binding for given bean definition
* `getProperty()` - resolve a property

### Writing a module

Given following classes:

{% highlight kotlin %}
class View(val presenter : Presenter)
interface Presenter
class MyPresenter(val repository : Repository) : Presenter
class DataRepository()
{% endhighlight %}

We can write it with a module:

{% highlight kotlin %}
val myModule = module {

    // Define a single instance of DataRepository
    single { DataRepository() }


    // define a submodule
    module("view"){

        // Define a factory for type Presenter (create a new instance each time)
        // Inject constructor with DataRepository
        factory<Presenter> { MyPresenter(get()) }
        
        // Define a single instance of View
        // Inject Presenter in constructor
        single { View(get()) }
    }
} 
{% endhighlight %}

Just start the module:

{% highlight kotlin %}
startKoin(listOf(myModule))
{% endhighlight %}

### Definitions & types

Below, how you can specify type binding and definition.

Given the following classes/interfaces:

{% highlight kotlin %}
class MyPresenter() : Presenter
interface Presenter
{% endhighlight %}

We can declare definition with single or factory keywords.

#### Single type definition

To describe a singleton instance of `MyPresenter`, we can write it:

{% highlight kotlin %}
single { MyPresenter(get()) }
{% endhighlight %}

To describe a singleton instance of `Presenter` with `MyPresenter` implementation, we can write it:

{% highlight kotlin %}
single<Presenter> { MyPresenter(get()) }
{% endhighlight %}

Beware that those definitions will only match type `Presenter`.

#### Matching multiple types

We can describe a singleton instance of `Presenter` with `MyPresenter` implementation, while matching both types:

{% highlight kotlin %}
single { MyPresenter(get()) } bind Presenter::class
{% endhighlight %}

This definition will match types `MyPresenter` and `Presenter`. You can sepcify extra binding with `bind` operator, as much as you want.

### Implicit naming & namespaces

If you don"t give a name to adefinition, Koin will give one related to its class name and path.

For example, the following factory `MyPresenter` can be resolved byts its name `view.MyPresenter`:

{% highlight kotlin %}
val myModule = module {
    
    module("view"){
        factory { MyPresenter() }
    }
} 

val presenter : MyPresenter = get(name = "view.MyPresenter")
{% endhighlight %}

Also convenient when you have several definition of the same type. You can resolve `ComponentA` with name `B.ComponentA` or `C.ComponentA`

{% highlight kotlin %}
module {
    module("B") {
        single { ComponentA() }
        single { ComponentB(get()) }
    }

    module("C") {
        single { ComponentA() }
        single { ComponentC(get()) }
    }
}

val a : ComponentA = get(name = "B.ComponentA")
{% endhighlight %}

You an aslo give a name to a definition, with the `name` attribute: `single(name="aName") { ... }`

### Using and combining modules

There is no import features: all definitions are lazy and executed at runtime. This means that you can compose definitions with multiple modules. The above module can be separated:

{% highlight kotlin %} 
val dataModule = module {

    // Define a single instance of DataRepository
    single<DataRepository>()
} 

// define module in /view path
val viewModule = module("view"){

    // Define a factory for type Presenter (create a new instance each time)
    // Inject constructor with DataRepository
    factory<Presenter> { create<MyPresenter>() }

    // Define a single instance of View
    // Inject Presenter in constructor
    single<View>()
}
{% endhighlight %}

Just start the module list:

{% highlight kotlin %}
startKoin(listOf(dataModule,viewModule))
{% endhighlight %}

### Dealing with generics

Koin definitions doesn't take in accounts generics type argument. For example, the module below tries to define 2 definitions of List:

{% highlight kotlin %}
module {
    single { ArrayList<Int>() }
    single { ArrayList<String>() }
}
{% endhighlight %}

Koin won't start with such definitions, understanding that you want to override one definition for the other.

To allow you, use the 2 definitions you will have to differentiate them via their name, or location (module). For example:

{% highlight kotlin %}
module {
    single(name="Ints") { ArrayList<Int>() }
    single(name="Strings") { ArrayList<String>() }
}
{% endhighlight %}

## Beyond Koin DSL

Below are some further readings:

* [Complete documentation of Koin DSL]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_koin_dsl)
* [Overriding definitions]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_overriding_a_definition_or_a_module)
* [Create instances at start]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_create_instances_at_start)
* [More about modules]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_koin_modules)
* [Modules visibility rules]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_hierarchy_visibility)
* [Checking your modules configuration with check() and dryRun()]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/koin-core/index.html#_checking_your_koin_configuration)
