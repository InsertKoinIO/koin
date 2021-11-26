---
title: Passing Parameters - Injected Parameters
---

In any definition, you can use injection parameters: parameters that will be injected and used by your definition.

## Passing values to inject

Given a definition, you can pass paramaters to that definition:

```kotlin
class Presenter(val a : A, val b : B)

val myModule = module {
    single { params -> Presenter(a = params.get(), b = params.get()) }
}
```

Parameters are sent to your definition with the `parametersOf()` function (each value seperated by comma): 

```kotlin
class MyComponent : View, KoinComponent {

    val a : A ...
    val b : B ... 

    // inject this as View value
    val presenter : Presenter by inject { parametersOf(a, b) }
}
```

## Defining an "injected parameter"

Below is an example of injection parameters. We established that we need a `view` parameter to build of `Presenter` class. We use the `params` function argument  to help retrieve our injected parqmeters:

```kotlin
class Presenter(val view : View)

val myModule = module {
    single { params -> Presenter(view = params.get()) }
}
```

You can also write your injected parameters directly with the parameters object, as destructured declaration:

```kotlin
class Presenter(val view : View)

val myModule = module {
    single { (view : View) -> Presenter(view) }
}
```

:::caution
 Even if the "destrutured" declaration is more conveient and readable, it's not type safe. Kotlin won't detect that passed type are in good orders if you have several values
:::

## Resolving injected parameters

Koin graph resolution (main tree of resolution of all definitions) also let you find your injected parameter. Just use the usual `get()` function:

```kotlin
class Presenter(val view : View)

val myModule = module {
    single { Presenter(get()) }
}
```


