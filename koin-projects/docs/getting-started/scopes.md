---
layout: docs
title: Scope API
description: Declare scoped instances in Koin
group: quick-references
toc: true
---

### What's a scope?
A scope is a fixed duration of time in which an object exists. When the scope context ends, any objects bound under that scope cannot be injected again (they are dropped from the container).

### Declare a scoped definition

By default in Koin, we have 3 kinds of scopes:

- `single` definition, create an object that persists with the entire container lifetime (can't be dropped).
- `factory` definition, create a new object each time. No persistence in the container (can't be shared).
- `scoped` definition, create an object whose persistence is tied to its associated scope lifetime.

### Declare and use a scope

A scope is a logical unit of scoped definitions.

For the given classes:

```kotlin
class ComponentA
class ComponentB(val a : class ComponentA)
```

We can write the following scope:

```kotlin
module {
    // define the scope with its qualifier "MY_SCOPE"
    scope(named("MY_SCOPE") {
        scoped { ComponentA() }
        scoped { ComponentB(get()) }
    }
}
```

### Create and use a scope instance

From any `Koin` instance (also available from your `KoinComponent` with `getKoin()`) the following functions are available for handling scope instances:

```kotlin
// create scope instance with ScopeID "myScopeId" for scope named "MY_SCOPE"
val myScope : Scope = koin.createScope("myScopeId", named("MY_SCOPE"))

// get instance ComponentA from scope
val a = myScope.get<ComponentA>()
// get instance ComponentB from scope and retrieve instance ComponentA from same scope
val b = myScope.get<ComponentB>()

// if you need, you can retrieve your scope instance by its id
val myScope : Scope = koin.getScope("myScopeId")

// close the scope when needed
myScope.close()
```

## Scope vs. Module unloading

Koin offers 2 possibilities for handling definitions & instances for a limited lifetime: Scope API & modules unload. What is the difference and when would you use them?

- `unloadKoinModules()` allows you to drop modules definitions (and instances). It's very useful for a dynamic module architecture approach, or when we need to load/unload/reload definitions.
- Scope API is dedicated to limited-lifetime definitions, creating a bunch of definitions for a given lifetime/purpose.

Using modules unload is simpler than wiring scopes, but you have to remember to unload/reload modules definitions each time you need. Also one difference, definitions are no longer reachable. Scope API is more dedicated to fine-grained lifetime limited instances, or multiple instance of the same scopes (sessions...).
