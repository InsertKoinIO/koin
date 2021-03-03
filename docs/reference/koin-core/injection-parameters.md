---
title: Injection Parameters
---

In any definition, you can use injection parameters: parameters that will be injected and used by your definition:

## Defining an injected parameter

Below is an example of injection parameters. We established that we need a `view` parameter to build of `Presenter` class. We use the `params` function argument  to help retrieve our injected parqmeters:

```kotlin
class Presenter(val view : View)

val myModule = module {
    single { params -> Presenter(view = params.get()) }
}
```

You can also let the Koin graph resolution find your injected parameter for you. Just use the usual `get()` function:

```kotlin
class Presenter(val view : View)

val myModule = module {
    single { Presenter(get()) }
}
```

Finally, you can also write your injected parameters directly with the parameters object, as destructured declaration:

```kotlin
class Presenter(val view : View)

val myModule = module {
    single { (view : View) -> Presenter(view) }
}
```

:::caution
 Even if the "destrutured" declaration is more conveient and readable, it's not type safe. Kotlinb won't detect that passed type are in good orders if you have several values
:::


## Passing values to inject

Given a definition that is using injected parameters:

```kotlin
class Presenter(val a : A, val b : B)

val myModule = module {
    single { params -> Presenter(a = params.get(), b = params.get()) }
}

Injection parameters are parameters passed through the resolution API with the `parametersOf()` function (each value seperated by comma): 

```kotlin
class MyComponent : View, KoinComponent {

    val a : A ...
    val b : B ... 

    // inject this as View value
    val presenter : Presenter by inject { parametersOf(a, b) }
}
```

