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

In this module, you can declare components as described below.

