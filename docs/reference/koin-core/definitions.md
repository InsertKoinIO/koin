---
title: Definitions
---

By using Koin, you describe definitions in modules. In this section we will see how to declare, organize & link your modules.

## Writing a module

A Koin module is the *space to declare all your components*. Use the `module` function to declare a Koin module:

```kotlin
val myModule = module {
   // your dependencies here
}
```

In this module, you can declare components as described below.

## Defining a singleton

Declaring a singleton component means that Koin container will keep a *unique instance* of your declared component. Use the `single` function in a module to declare a singleton:

```kotlin
class MyService()

val myModule = module {

    // declare single instance for MyService class
    single { MyService() }
}
```

## Defining your component within a lambda

`single`, `factory` & `scoped` keywords help you declare your components through a lambda expression. this lambda describe
the way that you build your component. Usually we instantiate components via their constructors, but you can also use any expression.

`single { Class constructor // Kotlin expression }`

The result type of your lambda is the main type of your component

## Defining a factory

A factory component declaration is a definition that will provide you a *new instance each time* you ask for this definition (this instance is not retained by Koin container, as it won't inject this instance in other definitions later). Use the `factory` function with a lambda expression to build a component.

```kotlin
class Controller()

val myModule = module {

    // declare factory instance for Controller class
    factory { Controller() }
}
```

:::info
 Koin container doesn't retain factory instances as it will give a new instance each time the definition is asked.
:::

## Resolving & injecting dependencies

Now that we can declare components definitions, we want to link instances with dependency injection. To *resolve an instance* in a Koin module, just use the `get()`
function to the requested needed component instance. This `get()` function is usually used into constructor, to inject constructor values.

:::info
 To make dependency injection with Koin container, we have to write it in *constructor injection* style: resolve dependencies in class constructors. This way, your instance will be created with injected instances from Koin.
:::

Let's take an example with several classes:

```kotlin
// Presenter <- Service
class Service()
class Controller(val view : View)

val myModule = module {

    // declare Service as single instance
    single { Service() }
    // declare Controller as single instance, resolving View instance with get()
    single { Controller(get()) }
}
```

## Definition: binding an interface

A `single` or a `factory` definition use the type from their given lambda definition i.e:  `single { T }`
The matched type of the definition is the only matched type from this expression.

Let's take an example with a class and implemented interface:

```kotlin
// Service interface
interface Service{

    fun doSomething()
}

// Service Implementation
class ServiceImp() : Service {

    fun doSomething() { ... }
}
```

In a Koin module we can use the `as` cast Kotlin operator as follows:

```kotlin
val myModule = module {

    // Will match type ServiceImp only
    single { ServiceImp() }

    // Will match type Service only
    single { ServiceImp() as Service }

}
```

You can also use the inferred type expression:

```kotlin
val myModule = module {

    // Will match type ServiceImp only
    single { ServiceImp() }

    // Will match type Service only
    single<Service> { ServiceImp() }

}
```

:::note
 This 2nd way of style declaration is preferred and will be used for the rest of the documentation.
:::

## Additional type binding

In some cases, we want to match several types from just one definition.

Let's take an example with a class and interface:

```kotlin
// Service interface
interface Service{

    fun doSomething()
}

// Service Implementation
class ServiceImp() : Service{

    fun doSomething() { ... }
}
```

To make a definition bind additional types, we use the `bind` operator with a class:

```kotlin
val myModule = module {

    // Will match types ServiceImp & Service
    single { ServiceImp() } bind Service::class
}
```

Note here, that we would resolve the `Service` type directly with `get()`. But if we have multiple definitions binding `Service`, we have to use the `bind<>()` function.

## Definition: naming & default bindings

You can specify a name to your definition, to help you distinguish two definitions about the same type:

Just request your definition with its name:

```kotlin
val myModule = module {
    single<Service>(named("default")) { ServiceImpl() }
    single<Service>(named("test")) { ServiceImpl() }
}

val service : Service by inject(qualifier = named("default"))
```

`get()` and `by inject()` functions let you specify a definition name if needed. This name is a `qualifier` produced by the `named()` function.

By default, Koin will bind a definition by its type or by its name, if the type is already bound to a definition.

```kotlin
val myModule = module {
    single<Service> { ServiceImpl1() }
    single<Service>(named("test")) { ServiceImpl2() }
}
```

Then:

- `val service : Service by inject()` will trigger the `ServiceImpl1` definition
- `val service : Service by inject(named("test"))` will trigger the `ServiceImpl2` definition


## Declaring injection parameters

In any definition, you can use injection parameters: parameters that will be injected and used by your definition:

```kotlin
class Presenter(val view : View)

val myModule = module {
    single{ (view : View) -> Presenter(view) }
}
```

In contrary to resolved dependencies (resolved with `get()`), injection parameters are *parameters passed through the resolution API*.
This means that those parameters are values passed with `get()` and `by inject()`, with the `parametersOf` function:


```kotlin
val presenter : Presenter by inject { parametersOf(view) }
```

Further reading in the [Injection Parameters Section](/docs/reference/koin-core/injection-parameters)


## Using definition flags

Koin DSL also proposes some flags.

### Create instances at start

A definition or a module can be flagged as `CreatedAtStart`, to be created at start (or when you want). First set the `createdAtStart` flag on your module
or on your definition.


CreatedAtStart flag on a definition

```kotlin
val myModuleA = module {

    single<Service> { ServiceImp() }
}

val myModuleB = module {

    // eager creation for this definition
    single<Service>(createdAtStart=true) { TestServiceImp() }
}
```

CreatedAtStart flag on a module:

```kotlin
val myModuleA = module {

    single<Service> { ServiceImp() }
}

val myModuleB = module(createdAtStart=true) {

    single<Service>{ TestServiceImp() }
}
```

The `startKoin` function will automatically create definitions instances flagged with `createdAtStart`.

```kotlin
// Start Koin modules
startKoin {
    modules(myModuleA,myModuleB)
}
```

:::info
if you need to load some definition at a special time (in a background thread instead of UI for example), just get/inject the desired components.
:::


### Dealing with generics

Koin definitions doesn't take in accounts generics type argument. For example, the module below tries to define 2 definitions of List:

```kotlin
module {
    single { ArrayList<Int>() }
    single { ArrayList<String>() }
}
```

Koin won't start with such definitions, understanding that you want to override one definition for the other.

To allow you, use the 2 definitions you will have to differentiate them via their name, or location (module). For example:

```kotlin
module {
    single(named("Ints")) { ArrayList<Int>() }
    single(named("Strings")) { ArrayList<String>() }
}
```

