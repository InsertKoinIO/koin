
Koin brings a simple API to let you define instances that are tied to a limit lifetime.

## What is a scope?

Scope is a fixed duration of time or method calls in which an object exists.
Another way to look at this is to think of scope as the amount of time an object’s state persists.
When the scope context ends, any objects bound under that scope cannot be injected again (they are dropped from the container).

## Scope definition

By default in Koin, we have 3 kind of scopes:

- `single` definition, create an object that persistent with the entire container lifetime (can't be dropped).
- `factory` definition, create a new object each time. Short live. No persistence in the container (can't be shared).
- `scoped` definition, create an object that persistent tied to the associated scope lifetime.

To declare a scoped definition, use the `scoped` function like follow. A scope gathers scoped definitions as a logical unit of time:

```kotlin
module {
    scope(named("A Scope Name")){
        scoped { Presenter() }
        // ...
    }
}
```

> A scope require a _qualifier_ to help name it. It can be eitehr a String Qualifier, either a TypeQualifier

Declaring a scope for a given type, can be done:

```kotlin
module {
    scope(named<MyType>()){
        scoped { Presenter() }
        // ...
    }
}
```

or can even declared directly like this:

```kotlin
module {
    scope<MyType>{
        scoped { Presenter() }
        // ...
    }
}
```

## Using a Scope

Let's have those classes

```kotlin
class A
class B
class C
```

### Declare a Scoped Instance

Let's scope `B` & `C` instances from `A`, `B` & `C` instanceds are tied to a `A` instance:

```kotlin
module {
    single { A() }
    scope<A> {
        scoped { B() }
        scoped { C() }
    }
}
```

### Create a scope and retrieve dependencies

Below is how we can create scope & retrieve dependencies

```kotlin
// Get A from Koin's main scope
val a : A = koin.get<A>()

// Get/Create Scope for `a`
val scopeForA = a.getOrCreateScope()

// Get scoped instances from `a`
val b = scopeForA.get<B>()
val c = scopeForA.get<C>()
```

We use the `getOrCreateScope` function, that will create a scope define by the type.

> Note here that `scopeForA` is tied to `a` instance object

### Destroy scope and linked instances

```kotlin
// Destroy `a` scope & drop `b` & `c`
a.closeScope()
```

We use the `closeScope` function, to drop current scope & related scoped instances.

## More about Scope API

### Working with a scope

A scope instance can be created with as follow: `val scope = koin.createScope(id,qualifier)`. `id` is a ScopeId and `qualifier` the scope qualifier.

To resolve a dependency using the scope we can do it like:

* `val presenter = scope.get<Presenter>()` - directly using the get/inject functions from the scope instance

### Create & retrieve a scope

From your `Koin` instance you can access:

- `createScope(id : ScopeID, scopeName : Qualifier)` - create a closed scope instance with given id and scopeName
- `getScope(id : ScopeID)` - retrieve a previously created scope with given id
- `getOrCreateScope(id : ScopeID, scopeName : Qualifier)` - create or retrieve if already created, the closed scope instance with given id and scopeName

!> Make the difference between a scope instance id, which is the id to find your scope over all your scopes, and the scope name, which is the reference to the tied scope group name.

### Resolving dependencies within a scope

The interest of a scope is to define a common logical unit of time for scoped definitions. It's allow also to resolve definitions from within the given scope

```kotlin
// given the classes
class ComponentA
class ComponentB(val a : ComponentA)

// module with scope
module {
    
    scope(named("A_SCOPE_NAME")){
        scoped { ComponentA() }
        // will resolve from current scope instance
        scoped { ComponentB(get()) }
    }
}
```

The dependency resolution is then straight forward:

```kotlin
// create an closed scope instance "myScope1" for scope "A_SCOPE_NAME"
val myScope1 = koin.createScope("myScope1",named("A_SCOPE_NAME"))

// from the same scope
val componentA = myScope1.get<ComponentA>()
val componentB = myScope1.get<ComponentB>()
```

!> By default, all scope fallback to resolve in main scope if no definition is found in the current scope

### Closing a scope

Once your scope instance is finished, just closed it with the `close()` function:

```kotlin
// from a KoinComponent
val session = getKoin().createScope("session")

// use it ...

// close it
session.close()
```

!> Beware that you can't inject instances anymore from a closed scope.

### Scope callback -- TODO


