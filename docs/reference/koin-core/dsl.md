---
title: Koin DSL
---

Thanks to the power of Kotlin language, Koin provides a DSL to help your describe your app instead of annotate it or generate code for it. With its Kotlin DSL, Koin offers a smart functional API to achieve to prepare your dependency injection.

## Application & Module DSL

Koin offers several keywords to let you describe the elements of a Koin Application:

- Application DSL, to describe the Koin container configuration
- Module DSL, to describe the components that have to be injected

## Application DSL

A `KoinApplication` instance is a Koin container instance configuration. This will let your configure logging, properties loading and modules.

To build a new `KoinApplication`, use the following functions:

* `koinApplication { }` - create a `KoinApplication` container configuration 
* `startKoin { }` - create a `KoinApplication` container configuration and register it in the `GlobalContext` to allow the use of GlobalContext API

To configure your `KoinApplication` instance, you can use any of the following functions :

* `logger( )` - describe what level and Logger implementation to use (by default use the EmptyLogger)
* `modules( )` - set a list of Koin modules to load in the container (list or vararg list)
* `properties()` - load HashMap properties into Koin container
* `fileProperties( )` - load properties from given file into Koin container
* `environmentProperties( )` - load properties from OS environment into Koin container
* `createEagerInstances()` - create eager instances (Single definitions marked as `createdAtStart`)

## KoinApplication instance: Global vs Local

As you can see above, we can describe a Koin container configuration in 2 ways: `koinApplication` or `startKoin` function. 

- `koinApplication` describe a Koin container instance
- `startKoin` describe a Koin container instance and register it in Koin `GlobalContext`

By registering your container configuration into the `GlobalContext`, the global API can use it directly. Any `KoinComponent` refers to a `Koin` instance. By default we use the one from `GlobalContext`.

Check chapters about Custom Koin instance for more information.

## Starting Koin

Starting Koin means run a `KoinApplication` instance into the `GlobalContext`.

To start Koin container with modules, we can just use the `startKoin` function like that:

```kotlin
// start a KoinApplication in Global context
startKoin {
    // declare used logger
    logger()
    // declare used modules
    modules(coffeeAppModule)
}
```

## Module DSL

A Koin module gather definitions that you will inject/combine for your application. To create a new module, just use the following function:

* `module { // module content }` - create a Koin Module

To describe your content in a module, you can use the following functions:

* `factory { //definition }` - provide a factory bean definition
* `single { //definition  }` - provide a singleton bean definition (also aliased as `bean`)
* `get()` - resolve a component dependency (also can use name, scope or parameters)
* `bind()` - add type to bind for given bean definition
* `binds()` - add types array for given bean definition
* `scope { // scope group }` - define a logical group for `scoped` definition 
* `scoped { //definition }`- provide a bean definition that will exists only in a scope

Note: the `named()` function allow you to give a qualifier either by a string, an enum or a type. It is used to name your definitions.

### Writing a module

A Koin module is the *space to declare all your components*. Use the `module` function to declare a Koin module:

```kotlin
val myModule = module {
   // your dependencies here
}
```

In this module, you can declare components as decribed below.

### withOptions - DSL Options (since 3.2)

Like for new [Constructor DSL](./dsl-update.md) definitions, you can specify definition options on "regular" definitions with
the `withOptions` operator:

```kotlin
module {
    single { ClassA(get()) } withOptions { 
        named("qualifier")
        createdAtStart()
    }
}
```

Within this option lambda, you can specify the following options:

* `named("a_qualifier")` - give a String qualifier to the definition
* `named<MyType>()` - give a Type qualifier to the definition
* `bind<MyInterface>()` - add type to bind for given bean definition
* `binds(arrayOf(...))` - add types array for given bean definition
* `createdAtStart()` - create single instance at Koin start

### Module Includes (since 3.2)

A new function `includes()` is available in the `Module` class, which lets you compose a module by including other modules in an organized and structured way.

The two prominent use cases of the new feature are:
- Split large modules into smaller and more focused ones.
- In modularized projects, it allows you more fine control over a module visibility (see examples below).

How does it work? Let's take some modules, and we include modules in `parentModule`:

```kotlin
// `:feature` module
internal val childModule1 = module { /* Other definitions here. */ }
internal val childModule2 = module { /* Other definitions here. */ }
public val parentModule = module { includes(childModule1,childModule2) }

// `:app` module
startKoin { modules(parentModule) }
```

Notice we do not need to set up all modules explicitly: by including `parentModule`, all the modules declared in the `includes` will be automatically loaded (`childModule1` and `childModule2`).  In other words, Koin is effectively loading: `parentModule`, `childModule1` and `childModule2`.

An important detail to observe is that you can use `includes` to add `internal` and `private` modules too - that gives you flexibility over what to expose in a modularized project.

:::info
Module loading is now optimized to flatten all your module graphs and avoid duplicated definitions of modules.
:::

Finally, you can include multiple nested or duplicates modules, and Koin will flatten all the included modules removing duplicates:

```kotlin
// :feature module
internal val dataModule = module { /* Other definitions here. */ }
internal val domainModule = module { /* Other definitions here. */ }
public val featureModule1 = module { includes(domainModule, dataModule) }
public val featureModule2 = module { includes(domainModule, dataModule) }

// `:app` module
startKoin { modules(featureModule1, featureModule2) }
```

Notice that all modules will be included only once: `dataModule`, `domainModule`, `featureModule1`, `featureModule2`.
