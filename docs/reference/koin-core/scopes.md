---
title: Scopes
---

Koin brings a simple API to let you define instances that are tied to a limit lifetime.

## What is a scope?

Scope is a fixed duration of time or method calls in which an object exists.
Another way to look at this is to think of scope as the amount of time an object’s state persists.
When the scope context ends, any objects bound under that scope cannot be injected again (they are dropped from the container).

## Scope definition

By default, in Koin, we have 3 kind of scopes:

- `single` definition, create an object that persistent with the entire container lifetime (can't be dropped).
- `factory` definition, create a new object each time. Short live. No persistence in the container (can't be shared).
- `scoped` definition, create an object that persistent tied to the associated scope lifetime.

To declare a scoped definition, use the `scoped` function like follow. A scope gathers scoped definitions as a logical unit of time.

Declaring a scope for a given type, we need to use the `scope` keyword:

```kotlin
module {
    scope<MyType>{
        scoped { Presenter() }
        // ...
    }
}
```

### Scope Id & Scope Name

A Koin Scope is defined by its: 

- scope name - scope's qualifier
- scope id - unique identifier of the scope instance

:::note
 `scope<A> { }` is equivalent to `scope(named<A>()){ } `, but more convenient to write. Note that you can also use a string qualifier like: `scope(named("SCOPE_NAME")) { }`
:::

From a `Koin` instance, you can access:

- `createScope(id : ScopeID, scopeName : Qualifier)` - create a closed scope instance with given id and scopeName
- `getScope(id : ScopeID)` - retrieve a previously created scope with given id
- `getOrCreateScope(id : ScopeID, scopeName : Qualifier)` - create or retrieve if already created, the closed scope instance with given id and scopeName

:::note
By default calling `createScope` on an object, doesn't pass the "source" of the scope. You need to pass it as parameters: `T.createScope(<source>)`
:::

### Scope Component: Associate a scope to a component [2.2.0]

Koin has the concept of `KoinScopeComponent` to help bring a scope instance to its class:

```kotlin
class A : KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }
}

class B
```

The `KoinScopeComponent` interface brings several extensions:
- `createScope` to create scope from current component's scope Id & name
- `get`, `inject` - to resolve instances from scope (equivalent to `scope.get()` & `scope.inject()`)

Let's define a scope for A, to resolve B:

```kotlin
module {
    scope<A> {
        scoped { B() } // Tied to A's scope
    }
}
```

We can then resolve instance of `B` directly thanks to `org.koin.core.scope` `get` & `inject` extensions:

```kotlin
class A : KoinScopeComponent {
    override val scope: Scope by lazy { newScope(this) }

    // resolve B as inject
    val b : B by inject() // inject from scope

    // Resolve B
    fun doSomething(){
        val b = get<B>()
    }

    fun close(){
        scope.close() // don't forget to close current scope
    }
}
```

### Resolving dependencies within a scope

To resolve a dependency using the scope's `get` & `inject` functions:  `val presenter = scope.get<Presenter>()` 

The interest of a scope is to define a common logical unit of time for scoped definitions. It's allow also to resolve definitions from within the given scope

```kotlin
// given the classes
class ComponentA
class ComponentB(val a : ComponentA)

// module with scope
module {
    
    scope<A> {
        scoped { ComponentA() }
        // will resolve from current scope instance
        scoped { ComponentB(get()) }
    }
}
```

The dependency resolution is then straight forward:

```kotlin
// create scope
val myScope = koin.createScope<A>()

// from the same scope
val componentA = myScope.get<ComponentA>()
val componentB = myScope.get<ComponentB>()
```

:::info
 By default, all scopes fallback to resolve in the main scope if no definition is found in the current scope
:::

### Close a scope

Once you are finished with your scope instance, just close it with the `close()` function:

```kotlin
// from a KoinComponent
val scope = getKoin().createScope<A>()

// use it ...

// close it
scope.close()
```

:::info
 Beware that you can't inject instances anymore from a closed scope.
:::

### Getting scope's source value

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

:::note
 Difference between `getSource()` and `get()`: getSource will directly get the source value. Get will try to resolve any definition, and fallback to source
value if possible. `getSource()` is then more efficient in terms of performances.
:::

### Scope Linking

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

With scope linking API, we can allow to resolve B's scope instance C, directly from A's scope. For this we use `linkTo()` on scope instance:

```kotlin
val a = koin.get<A>()
// let's get B from A's scope
val b = a.scope.get<B>()
// let's link A' scope to B's scope
a.scope.linkTo(b.scope)
// we got the same C instance from A or B scope
assertTrue(a.scope.get<C>() == b.scope.get<C>())
```

### Scope Archetypes

Scope "Archetypes" are scope spaces for a generic kind of classes. For example, you can have Scope Archetypes for Android (Activity, Fragment, ViewModel) or even Ktor (RequestScope).
Scope Archetype is Koin's `TypeQualifier` pass to different APIs, to request scope space for a given

An archetype consists of:
- Module DSL extension, to declare a scope for a given type:
```kotlin
// Declare a scope archetype for ActivityScopeArchetype (TypeQualifier(AppCompatActivity::class)
fun Module.activityScope(scopeSet: ScopeDSL.() -> Unit) {
    val qualifier = ActivityScopeArchetype
    ScopeDSL(qualifier, this).apply(scopeSet)
}
```
- An API that requests a Scope with the given specific Scope Archetype TypeQualifier:
```kotlin
// Create scope with ActivityScopeArchetype archetype
val scope = getKoin().createScope(getScopeId(), getScopeName(), this, ActivityScopeArchetype)
```

