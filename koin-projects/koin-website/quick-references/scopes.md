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

### Declare and use a scope

A scope is a logical unit of scoped definitions.

For the given classes:

{% highlight kotlin %}
class ComponentA
class ComponentB(val a : class ComponentA)
{% endhighlight %}

We can write the following scope:

{% highlight kotlin %}
module {
    // define the scope with its qualifier "MY_SCOPE"
    scope(named("MY_SCOPE") {
        scoped { ComponentA() }
        scoped { ComponentB(get()) }
    }
}
{% endhighlight %}

### Create and use a scope instance

From any `Koin` instance or also available from your `KoinComponent` with `getKoin()`, the following functions allows to handle scope instances:

{% highlight kotlin %}
// create scope instance with ScopeID "myScopeId" for scope naled "MY_SCOPE"
val myScope : Scope = koin.createScope("myScopeId", named("MY_SCOPE")

// get instance ComponentA from scope
val a = myScope.get<ComponentA>()
// get instance ComponentB from scope and retrieve instance ComponentA from same scope
val b = myScope.get<ComponentB>()

// if you need, you can retrieve your scope instance by its id
val myScope : Scope = koin.getScope("myScopeId")

// close the scope when needed
myScope.close()
{% endhighlight %}

## Scope vs Module unloading

Koin gives 2 possibilties to handle definitions & instances for a limited lifetime: Scope API & modules unload. What is the difference and when to use them?

- `unloadKoinModules()` allows to drop modules definitions (and instances). it's very usefull for dymaic module architecture approach, or when we need to load/unload/reload definitions. 
- Scope API is dedicated to limited lifetime definitions, creating bunch of definitions for a given lifetime/purpose

Using modules unload is more simpler that wiring scopes, but you have to unload/reload modules definitions each time you need. Also one difference, definitions are no longer reachables. Scope API is more dedcaited to fine grained lifetime limited instances, or multiple instance of the same scopes (sessions...).
