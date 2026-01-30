---
title: Autowire DSL
---

Koin offers an autowire DSL that allows you to target a class constructor directly and automatically wire dependencies.

:::tip
If you're using the **Koin Compiler Plugin**, consider using the [Compiler Plugin DSL](/docs/setup/compiler-plugin) which provides similar auto-wiring with additional compile-time safety.
:::

## Classic Autowire DSL

For a given class `ClassA` with dependencies:

```kotlin
class ClassA(val b: ClassB, val c: ClassC)
class ClassB()
class ClassC()
```

Declare components targeting the class constructor:

```kotlin
import org.koin.dsl.*

module {
    singleOf(::ClassA)
    singleOf(::ClassB)
    singleOf(::ClassC)
}
```

No need to specify dependencies with `get()` function!

:::info
Use `::` before your class name to target the constructor.
:::

:::note
Your constructor is filled automatically with all required dependencies. Avoid using default values as Koin will try to resolve all parameters.
:::

## Comparison with Compiler Plugin DSL

| Classic Autowire | Compiler Plugin |
|------------------|-----------------|
| `singleOf(::ClassA)` | `single<ClassA>()` |
| `factoryOf(::ClassA)` | `factory<ClassA>()` |
| `scopedOf(::ClassA)` | `scoped<ClassA>()` |
| Package: `org.koin.dsl` | Package: `org.koin.plugin.module.dsl` |

The Compiler Plugin DSL provides the same auto-wiring capability with additional compile-time verification.

## Available Keywords

The following autowire keywords are available to build your definition from constructor:

* `factoryOf` - equivalent of `factory { }` - factory definition
* `singleOf` - equivalent of `single { }` - single definition
* `scopedOf` - equivalent of `scoped { }` - scoped definition

:::info
Be sure to not use any default value in your constructor, as Koin will try to fill every parameter with it.
:::

## DSL Options

Any autowire DSL definition can also open some options within a lambda:

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
* `bind<MyInterface>()` - add type to bind for given definition
* `binds(listOf(...))` - add types list for given definition
* `createdAtStart()` - create single instance at Koin start

You can also use `bind` or `binds` operator, without any need of lambda:

```kotlin
module {
    singleOf(::ClassA) bind InterfaceA::class
}
```

## Injected Parameters

With autowire DSL declarations, you can still use injected parameters. Koin will look in injected parameters and current dependencies to try to inject your constructor.

Like following:

```kotlin
class MyFactory(val id : String)
```

declared with autowire DSL:

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
Koin Reflection DSL is now deprecated. Please use Koin Autowire DSL above
:::
