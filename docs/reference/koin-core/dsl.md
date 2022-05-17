---
title: Koin DSL
---

Thanks to the power of the Kotlin language, Koin provides a Domain-specific Language (DSL) to help your describe your application. No annotations or code generation is needed. With its Kotlin DSL, Koin offers a smart functional API to achieve to configure dependency injection.

## Application & Module DSL

Koin offers two sets of languages to let you describe the elements of your Koin Application:

- Application DSL, to describe the Koin container configuration
- Module DSL, to describe the components that have to be injected

## Application DSL

A `KoinApplication` instance is a Koin configuration and container. It will allow you to configure logging, property loading, and module declarations.

To build a new `KoinApplication`, use the following functions:

* `koinApplication { }` - create a `KoinApplication` container configuration 
* `startKoin { }` - create a `KoinApplication` container configuration and register it in the `GlobalContext` to allow the use of GlobalContext API

Configure your `KoinApplication` instance in the closure with any of the following functions:

* `logger( )` - describe what level and Logger implementation to use (`EmptyLogger` by default)
* `modules( )` - provide a list of Koin modules to load in the container (list or vararg list)
* `properties()` - load a `HashMap` of properties into the Koin container
* `fileProperties( )` - load properties from a file into the Koin container
* `environmentProperties( )` - load properties from the OS environment into the Koin container
* `createEagerInstances()` - create eager instances; single definitions marked as `createdAtStart`

## KoinApplication instance: Global vs Local

We can describe a Koin container configuration in two ways: `koinApplication` or `startKoin` functions. 

- `koinApplication` describes a Koin container instance
- `startKoin` describes a Koin container instance and registers it in the Koin `GlobalContext`

By registering your container configuration into the `GlobalContext`, it can be used directly with the global API. All `KoinComponent` instances reference a `Koin` instance. By default we use the one from `GlobalContext`.

Read chapters about Custom Koin instance for more information.

## Starting Koin

Starting Koin means to install a `KoinApplication` instance into the `GlobalContext`.

To start a Koin container with modules, we use the `startKoin` function, as in this example:

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

A Koin module gathers definitions that will be injected/combined in your application. To create a new module, use these function:

* `module { /* module content */ }` - create a Koin Module

To describe your content in a module, use the following functions:

* `factory { /* definition */ }` - provide a factory bean definition
* `single { //definition  }` - provide a singleton bean definition (also aliased as `bean`)
* `get()` - resolve a component dependency (you also can use name, scope or parameters)
* `bind()` - bind a type to a bean definition
* `binds()` - binds an array of types to a set of bean definition
* `scope { // scope group }` - define a logical group for `scoped` definitions 
* `scoped { //definition }`- provide a bean definition that will exists only in the associated scope

Note: the `named()` function allows you to include a qualifier, either with a string, an enum, or a type. It is used to name your definitions.

### Writing a module

A Koin module is the *place to declare all your components*. Use the `module` function to declare a Koin module:

```kotlin
val myModule = module {
   // your dependencies here
}
```

In this module, you can declare components as described below.
