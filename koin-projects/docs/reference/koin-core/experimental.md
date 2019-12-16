
The `koin-core-ext` brings extensions & experimental features to Koin.

!> Those features are experimental

## Better definition declaration (Experimental)

Koin DSL can be seen as "manual", while you must fill constructors with "get()" function to resolve needed instances. When your definition don't need any special constructor integration (injection paarameters
or special scope Id), we can go with more compact writing style thanks to API below.

?> Using reflection is not costless. it replaces what you don"t want to write with reflection code (finding primary constructors, injecting parameters...). Mind it before using it, if you are on
performances constraints platform (Android for example)

## Building any instance with create()

The first introduced function is the `create()` function.

Instead of declaring a definition with instantiating its constructor and retrieving instances with get()

```kotlin
module {
    single { ComponentA(get() ...) }
}
```

You can use instead, the `create()` function to build an instance from its primary constructor, and fill the needed dependencies.

```kotlin
module {
    single { create<ComponentA>() }
}
```

## Simpler DSL for definitions

You can also use the more "compact" notation that will use the `create()` function. Just use the single function without any expression:

```kotlin
module {
    single<ComponentA>()
}
```

If you have an implementation type and want to resolve with a target type, you can use the following `singleBy` function:

```kotlin
module {
    singleBy<Target,Implementation>()
}
```

?> Works for single, factory & scope

!> If you use custom constructors expression like injection parameters or others, don't use the reflection API.
