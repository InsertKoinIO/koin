---
layout: docs
title: koin-core
description: declare, start & inject
group: quick-references
toc: true
---

Below is a short description of `koin-core` features.

### Setup

{% highlight gradle %}
// Add Jcenter to your repositories if needed
repositories {
    jcenter()    
}
dependencies {
    
    compile 'org.koin:koin-core:{{ site.current_version }}'
}
{% endhighlight %}

### Declare a module

To declare a Koin module, we use the [Koin DSL]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-dsl/). Below is a short example:

{% highlight kotlin %}
val helloModule = module {

    single { HelloMessageData() }

    single<HelloService> { HelloServiceImpl(get()) }
}
{% endhighlight %}

All components described in modules can/should be injected by constructor.

### Start Koin

Once you have described your application components in modules, you are ready to start Koin with your list of modules:

{% highlight kotlin %}
fun main(vararg args: String) {

    startKoin(listOf(helloModule))
}
{% endhighlight %}

From this function, you can specify:

* useEnvironmentProperties - use the properties from your environement
* useKoinPropertiesFile - use the koin.properties file
* extraProperties - additional map of properties
* logger: Logger - Koin logger
* createOnStart: Boolean - create definitions tagged with `createOnStart`


### Inject with KoinComponents

Tag your class with the `KoinComponent` interface to unlock Koin features:

* `by inject()` - lazy inject an instance
* `get()` - retrieve an instance
* `getProperty()` - get a Koin property
* `release()` - release instances from a module

We can inject the module above into class properties:

{% highlight kotlin %}
class HelloApp : KoinComponent {

    // lazy inject property
    val helloService: HelloServiceImpl by inject()

    fun sayHello(){
        helloService.sayHello()
    }
}
{% endhighlight %}

And we just need to start Koin and run our class:

{% highlight kotlin %}
fun main(vararg args: String) {

    // Start Koin
    startKoin(listOf(helloModule))

    // Run our class
    HelloApp().sayHello()
}
{% endhighlight %}

### Using scopes with the Scope API

#### What's a scope?

A scope is a fixed duration of time in which an object exists. When the scope context ends, any objects bound under that scope cannot be injected again (they are dropped from the container).

#### Declare a scope definition

By default in Koin, we have 3 kind of scopes:

- `single` definition, create an object that persistent with the entire container lifetime (can't be dropped).
- `factory` definition, create a new object each time. No persistence in the container (can't be shared).
- `scope` definition, create an object that persistent tied to the associated scope lifetime.

To declare a scope definition, use the `scope` function:

{% highlight kotlin %}
module {
    scope("session") { Presenter() }
}
{% endhighlight %}


#### Using a scope

From a `KoinComponent` class, just use the `getKoin()` function to have access to following functions

- `createScope(id : String)` - create a scope with given id in the Koin scope registry
- `getScope(id : String)` - retrieve a previously created scope with given id, from the Koin scope registry
- `getOrCreateScope(id : String)` - create or retrieve if already created, the scope with given id in the Koin scope registry

{% highlight kotlin %}
// create a scope in Koin's registry
getKoin().createScope("session")

// will return the same instance of Presenter until scope 'session' is closed
val presenter = get<Presenter>()
{% endhighlight %}

Once you have created your `Scope` instance, let resolve our components!

Once your scope is finished, just closed it with the `Scope.close()` function:

{% highlight kotlin %}
// from a KoinComponent
val session = getKoin().createScope("session")
// will return the same instance of Presenter until 'session' is closed
val presenter = get<Presenter>()

// close it
session.close()
// instance of presenter has been dropped
{% endhighlight %}

#### Injecting scope definition

If one of your definition need to inject a scope instance, just resolve it and be sure to have created the right scope before:

{% highlight kotlin %}
class Presenter(val userSession : UserSession)
{% endhighlight %}

Just inject it into constructor, with teh right scope:

{% highlight kotlin %}
module {
    // Shared user session data
    scope("session") { UserSession() }
    // Inject UserSession instance from "session" Scope
    factory { Presenter(get())}
}

getKoin().createScope("session")
val presenter = get<Presenter>()
{% endhighlight %}

### Close Koin

You can close all the Koin resources and drop instances & definitions. For this you can use the `closeKoin()` function from anywhere.

### (Deprecated) Releasing single instances from a path

From a KoinComponent, you can the `release()` function to release single instances for a given path. For example; 

{% highlight kotlin %}
module {
    module(path = "A") {
        single { ComponentA() }

        module(path = "B") {
            single { ComponentB() }

            module(path = "C") {
                single { ComponentC() }
            }
        }
    }
}
{% endhighlight %}

- release("A"): will release ComponentA, ComponentB & ComponentC
- release("B"): will release ComponentB & ComponentC
- release("C"): will release ComponentC

IMPORTANT: This API is deprecated. In next version, Koin won't allow to drop single. Better use the Scope API

## More about core features

Below are some further readings:

* [Load Koin modules without startKoin()]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_loading_modules_without_startkoin_function)
* [Using properties]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_properties)
* [Koin Logging]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_logging_koin_activity)
* [More about using KoinComponents]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_koin_components)
* [Using Scopes]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_using_scopes)
