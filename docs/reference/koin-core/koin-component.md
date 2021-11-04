---
title: Koin Component
---

Koin is a DSL to help describe your modules & definitions, a container to make definition resolution. What we need now is
an API to retrieve our instances outside of the container. That's the goal of Koin components.

:::info
 The `KoinComponent` interface is here to help you retrieve instances directly from Koin. Be careful, this links your class to the Koin container API. Avoid to use it on classes that you can declare in `modules`, and prefer constructor injection
:::

## Create a Koin Component

To give a class the capacity to use Koin features, we need to *tag it* with `KoinComponent` interface. Let's take an example.

A module to define MyService instance
```kotlin
class MyService

val myModule = module {
    // Define a singleton for MyService
    single { MyService() }
}
```

we start Koin before using definition.

Start Koin with myModule

```kotlin
fun main(vararg args : String){
    // Start Koin
    startKoin {
        modules(myModule)
    }

    // Create MyComponent instance and inject from Koin container
    MyComponent()
}
```

Here is how we can write our `MyComponent` to retrieve instances from Koin container.

Use get() & by inject() to inject MyService instance

```kotlin
class MyComponent : KoinComponent {

    // lazy inject Koin instance
    val myService : MyService by inject()

    // or
    // eager inject Koin instance
    val myService : MyService = get()
}
```

## Unlock the Koin API with KoinComponents

Once you have tagged your class as `KoinComponent`, you gain access to:

* `by inject()` - lazy evaluated instance from Koin container
* `get()` - eager fetch instance from Koin container
* `getProperty()`/`setProperty()` - get/set property


## Retrieving definitions with get & inject

Koin offers two ways of retrieving instances from the Koin container:

* `val t : T by inject()` - lazy evaluated delegated instance
* `val t : T = get()` - eager access for instance

```kotlin
// is lazy evaluated
val myService : MyService by inject()

// retrieve directly the instance
val myService : MyService = get()
```

:::note
 The lazy inject form is better to define property that need lazy evaluation.
:::

## Resolving instance from its name

If you need you can specify the following parameter with `get()` or `by inject()`

* `qualifier` - name of the definition (when specified name parameter in your definition)

Example of module using definitions names:

```kotlin
val module = module {
    single(named("A")) { ComponentA() }
    single(named("B")) { ComponentB(get()) }
}

class ComponentA
class ComponentB(val componentA: ComponentA)
```

We can make the following resolutions:

```kotlin
// retrieve from given module
val a = get<ComponentA>(named("A"))
```
