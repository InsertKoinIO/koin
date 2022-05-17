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
val childModule1 = module {
    /* Other definitions here. */
}
val childModule2 = module {
    /* Other definitions here. */
}
val parentModule = module {
    includes(childModule1, childModule2)
}

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
val dataModule = module {
    /* Other definitions here. */
}
val domainModule = module {
    /* Other definitions here. */
}
val featureModule1 = module {
    includes(domainModule, dataModule)
}
val featureModule2 = module {
    includes(domainModule, dataModule)
}

// `:app` module
startKoin { modules(featureModule1, featureModule2) }
```

Notice that all modules will be included only once: `dataModule`, `domainModule`, `featureModule1`, `featureModule2`.
