
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

> A scope require a _qualifier_ to help name it. It can be either a String Qualifier, either a TypeQualifier

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

Let's scope `B` & `C` instances from `A`, `B` & `C` instances are tied to a `A` instance:

```kotlin
module {
    factory { A() }
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

### Using the `scope` property

```kotlin
// Get A from Koin's main scope
val a : A = koin.get<A>()

// Get scoped instances from `a`
val b = a.scope.get<B>()
val c = a.scope.get<C>()
```

!> Be careful to not use `scope` if you want to use a Scope for a special component, like Android `lifecycleScope` a scope tied to the Android lifecycle

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


### Getting scope's source value [2.1.4]

Koin Scope API in 2.1.4 allow you to pass the original source of a scope, in a definition. Let's take an example below.
Let's have a singleton instance `A`:

```kotlin
class A
class BofA(val a : A)

module {
    single { A() }
    scope<A> {
        scoped { BofA(getSource() /* or even get() */) }

    }
}
```

By creating A's scope, we can forward the reference of the scope's source (A instance), to underlying definitions of the scope: `scoped { BofA(getSource()) }` or even `scoped { BofA(get()) }`

This in order to avoid cascading parameter injection, and just retrieve our source value directly in scoped definition.

```kotlin
val a = koin.get<A>()
val b = a.scope.get<BofA>()
assertTrue(b.a == a)
```

> Difference between `getSource()` and `get()`: getSource will directly get the source value. Get will try to resolve any definition, and fallback to source
value if possible. `getSource()` is then more efficient in terms of performances.


### Scope Linking [2.1.0]

Koin Scope API in 2.1 allow you to link a scope to another, and then allow to resolve joined definition space. Let's take an example.
Here we are defining, 2 scopes spaces: a scope for A and a scope for B. In A's scope, we don't have access to C (defined in B's scope).

```kotlin
module {
    single { A() }
    scope<A> {
        scoped { B() }
    }
    scope<B> {
        scoped { C() }
    }
}
```

With scope linking API, we can allow to resolve B's scope instance C, directly from A'scope. For this we use `linkTo()` on scope instance:

```kotlin
val a = koin.get<A>()
// let's get B from A's scope
val b = a.scope.get<B>()
// let's link A' scope to B's scope
a.scope.linkTo(b.scope)
// we got the same C instance from A or B scope
assertTrue(a.scope.get<C>() == b.scope.get<C>())
```

### Scope callback -- TODO


