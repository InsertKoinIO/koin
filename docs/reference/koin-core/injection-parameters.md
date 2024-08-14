---
title: Passing Parameters - Injected Parameters
---

In any definition, you can use injection parameters: parameters that will be injected and used by your definition.

## Passing values to inject

Given a definition, you can pass parameters to that definition:

```kotlin
class Presenter(val a : A, val b : B)

val myModule = module {
    single { params -> Presenter(a = params.get(), b = params.get()) }
}
```

Parameters are sent to your definition with the `parametersOf()` function (each value separated by comma):

```kotlin
class MyComponent : View, KoinComponent {

    val a : A ...
    val b : B ... 

    // inject this as View value
    val presenter : Presenter by inject { parametersOf(a, b) }
}
```

## Defining an "injected parameter"

Below is an example of injection parameters. We established that we need a `view` parameter to build of `Presenter` class. We use the `params` function argument  to help retrieve our injected parameters:

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
 Even if the "destructured" declaration is more convenient and readable, it's not type safe. Kotlin won't detect that passed type are in good orders if you have several values
:::

## Resolving injected parameters in order

Instead of using `get()` to resolve a parameter, if you have several parameters of the same type you can use the index as follows `get(index)` (also same as `[ ]` operator):

```kotlin
class Presenter(val view : View)

val myModule = module {
    
    single { p -> Presenter(p[0],p[1]) }
}
```

## Resolving injected parameters from graph

Koin graph resolution (main tree of resolution of all definitions) also let you find your injected parameter. Just use the usual `get()` function:

```kotlin
class Presenter(val view : View)

val myModule = module {
    single { Presenter(get()) }
}
```


## Injected parameters: indexed values or set (`3.4.3`)

In addition to `parametersOf`, the following API are accessible:

- `parameterArrayOf`: to use an array of value, and data will be uses by its index

```kotlin
val params = parameterArrayOf(1,2,3)
params.get<Int>() == 1
params.get<Int>() == 2
params.get<Int>() == 3
params.get<Int>() == 3
```

- `parameterSetOf`: to use a set of values, with different kinds. Doesn't use index to scroll values.

```kotlin
val params = parameterSetOf("a_string", 42)
params.get<Int>() == 42
params.get<String>() == "a_string"
params.get<Int>() == 42
params.get<String>() == "a_string"
```

The default function `parametersOf` is working with both index & set of values:

```kotlin
val params = parametersOf(1,2,"a_string")
params.get<String>() == "a_string"
params.get<Int>() == 1
params.get<Int>() == 2
params.get<Int>() == 2
params.get<String>() == "a_string"
```

:::note
  You can "cascade" parameter injection with `parametersOf` or `parameterArrayOf`, to consume value based on index. Or use `parametersOf` or `parameterSetOf` to cascading based on type to resolve. 
:::
