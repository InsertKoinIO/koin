---
layout: docs
title: Scope API
description: declare scoped instances in Koin
group: quick-references
toc: true
---

### What's a scope?
A scope is a fixed duration of time in which an object exists. When the scope context ends, any objects bound under that scope cannot be injected again (they are dropped from the container).

### Declare a scoped definition

By default in Koin, we have 3 kind of scopes:

- `single` definition, create an object that persistent with the entire container lifetime (can't be dropped).
- `factory` definition, create a new object each time. No persistence in the container (can't be shared).
- `scoped` definition, create an object that persistent tied to the associated scope lifetime.

To declare a scope definition, use the `scoped` function:

{% highlight kotlin %}
module {
    scope { Presenter() }
}
{% endhighlight %}

### Declare and use a scope

A scope is a logical unit of scoped definitions.

For the given classes:

{% highlight kotlin %}
class ComponentA
class ComponentB(val a : class ComponentA)
class ComponentC
{% endhighlight %}

We can write the following scope:

{% highlight kotlin %}
module {
    
    // define the scope "MY_SCOPE"
    scope("MY_SCOPE") {
        scoped { ComponentA() }
        scoped { ComponentB(get()) }
    }

    // scoped definition not tied to any scope
    // can be injected in any scope instance
    scoped { ComponentC() }
}
{% endhighlight %}

### Create and use a scope instance

From any `Koin` instance or also available from your `KoinComponent` with `getKoin()`, the following functions allows to handle scope instances:

{% highlight kotlin %}
// create scope instance "myScope" for scope "MY_SCOPE"
// you can create several scope isntances of the same scope definition
val myScope : ScopeInstance = koin.createScope("myScope", "MY_SCOPE")

// get instance ComponentA from scope
val a = myScope.get<ComponentA>()
// get instance ComponentB from scope and retrieve instance ComponentA from same scope
val b = myScope.get<ComponentB>()

// can also get instance from a scoped component definition not tied to a scope definition
val c = myScope.get<ComponentC>()

// if you need, you can retrieve your scope instance by its id
val myScope : ScopeInstance = koin.getScope("myScope")

// close the scope when needed
myScope.close()
{% endhighlight %}


## More about

Below are some further readings:


