---
title: Koin Extended DSL (3.2)
---


## Constructor DSL (Since 3.2)

Koin now offer a new kind of DSL keyword that allow you to target a class constructor directly, and avoid to to have type your definition within a lambda expression.

For a given class `ClassA` with following dependencies:

```kotlin
class ClassA(val b : ClassB, val c : ClassC)
class ClassB()
class ClassC()
```

you can now declare those components, directly targeting the `class constructor`:

```kotlin
module {
    singleOf(::ClassA)
    singleOf(::ClassB)
    singleOf(::ClassC)
}
```

Not need to specify dependencies in constructor anymore with `get()` function! 🎉

:::info
Be sure to use `::` before your class name, to target your class constructor
:::

:::note
Your constructor is filled automatically with all `get()`. Avoid using any default value as Koin will try to find it in the current graph.
:::

:::note
If you need to retrieve a "named" definition, you need to use the standard DSL with lambda and `get()` to specify the qualifier
:::

### Available Keywords

The following keywords are available to build your definition from constructor:

* `factoryOf` - equivalent of `factory { }` - factory definition
* `singleOf` - equivalent of `single { }` - single definition
* `scopedOf` - equivalent of `scoped { }` - scoped definition

:::info
Be sure to not use any default value in your constructor, as Koin will try to fill every parameter of it.
:::

### DSL Options

Any Constructor DSL Definition, can also open some option within a lambda:

```kotlin
module {
    singleOf(::ClassA) { 
        // definition options
        named("my_qualifier")
        bind<InterfaceA>()
        createdAtStart()
    }
}
```

Usual options and DSL keywords are available in this lambda:

* `named("a_qualifier")` - give a String qualifier to the definition
* `named<MyType>()` - give a Type qualifier to the definition
* `bind<MyInterface>()` - add type to bind for given bean definition
* `binds(arrayOf(...))` - add types array for given bean definition
* `createdAtStart()` - create single instance at Koin start

You can also use `bind` or `binds` operator, without any need of lambda:

```kotlin
module {
    singleOf(::ClassA) bind InterfaceA::class
}
```

### Injected Parameters

With such kind of declaration, you can still use injected parameters. Koin will look in injected parameters and current dependencies to try inject your constructor.

Like following:

```kotlin
class MyFactory(val id : String)
```

declared with Constructor DSL:

```kotlin
module {
    factoryOf(::MyFactory)
}
```

can be injected like this:

```kotlin
val id = "a_factory_id"
val factory = koin.get<MyFactory> { parametersOf(id)}
```

## Reflection Based DSL (Deprecated since 3.2)

:::caution
Koin Reflection DSL is now deprecated. Please Use Koin Constructor DSL above
:::

Koin DSL can be seen as "manual", while you must fill constructors with "get()" function to resolve needed instances. When your definition don't need any special constructor integration (injection paarameters or special scope Id), we can go with more compact writing style thanks to API below.

Just use the single function without any expression:

```kotlin
module {
    single<ComponentA>() // will be quivalent to single { ComponentA() }
}
```

If you have an implementation type and want to resolve with a target type, you can use the regular `bind` or `binds` function:

```kotlin
module {
    single<Implementation>() bind Target::class
}
```