---
title: Modules
---

By using Koin, you describe definitions in modules. In this section we will see how to declare, organize & link your modules.

## What is a module?

A Koin module is a "space" to gather Koin definition. It's declared with the `module` function.

```kotlin
val myModule = module {
    // Your definitions ...
}
```

## Using several modules

Components doesn't have to be necessarily in the same module. A module is a logical space to help you organize your definitions, and can depend on definitions from other
module. Definitions are lazy, and they are resolved only when a component is requesting them.

Let's take an example, with linked components in separate modules:

```kotlin
// ComponentB <- ComponentA
class ComponentA()
class ComponentB(val componentA : ComponentA)

val moduleA = module {
    // Singleton ComponentA
    single { ComponentA() }
}

val moduleB = module {
    // Singleton ComponentB with linked instance ComponentA
    single { ComponentB(get()) }
}
```

:::info 
Koin doesn't have any import concept. Koin definitions are lazy: a Koin definition is started with Koin container but is not instantiated. An instance is created only when a request for its type has been done.
:::

We just have to declare list of used modules when we start our Koin container:

```kotlin
// Start Koin with moduleA & moduleB
startKoin {
    modules(moduleA,moduleB)
}
```

Koin will then resolve dependencies from all given modules.

## Overriding definition or module (3.1.0+)

New Koin override strategy allow to override any definition by default. You don't need to specify `override = true` anymore in your module.

If you have 2 definitions in different modules, that have the same mapping, the last will override the current definition.

```kotlin
val myModuleA = module {
    single<Service> { ServiceImp() }
}
val myModuleB = module {
    single<Service> { TestServiceImp() }
}

startKoin {
    // TestServiceImp will override ServiceImp definition
    modules(myModuleA,myModuleB)
}
```

You can check in Koin logs, about definition mapping override.

You can specify to not allow overriding in your Koin application configuration with `allowOverride(false)`:

```kotlin
startKoin {
    // Forbid definition override
    allowOverride(false)
}
```

In the case of disabling override, Koin will throw an `DefinitionOverrideException` exception on any attempt of override.

## Sharing Modules

When using the `module { }` function, Koin preallocate all instance factories. If you need to share a module, please consider return your module with a function. 

```kotlin
fun sharedModule() = module {
    // Your definitions ...
}
```

This way, you share the definitions and avoid preallocating factories in a value.

## Overriding definition or module (before 3.1.0)

Koin won't allow you to redefine an already existing definition (type,name,path ...). You will get an error if you try this:

```kotlin
val myModuleA = module {

    single<Service> { ServiceImp() }
}

val myModuleB = module {

    single<Service> { TestServiceImp() }
}

// Will throw an BeanOverrideException
startKoin {
    modules(myModuleA,myModuleB)
}
```

To allow definition overriding, you have to use the `override` parameter:

```kotlin
val myModuleA = module {

    single<Service> { ServiceImp() }
}

val myModuleB = module {

    // override for this definition
    single<Service>(override=true) { TestServiceImp() }
}
```

```kotlin
val myModuleA = module {

    single<Service> { ServiceImp() }
}

// Allow override for all definitions from module
val myModuleB = module(override=true) {

    single<Service> { TestServiceImp() }
}
```

:::note
 Order matters when listing modules and overriding definitions. You must have your overriding definitions in last of your module list.
:::


## Linking modules strategies

*As definitions between modules are lazy*, we can use modules to implement different strategy implementation: declare an implementation per module.

Let's take an example, of a Repository and Datasource. A repository need a Datasource, and a Datasource can be implemented in 2 ways: Local or Remote.

```kotlin
class Repository(val datasource : Datasource)
interface Datasource
class LocalDatasource() : Datasource
class RemoteDatasource() : Datasource
```

We can declare those components in 3 modules: Repository and one per Datasource implementation:

```kotlin
val repositoryModule = module {
    single { Repository(get()) }
}

val localDatasourceModule = module {
    single<Datasource> { LocalDatasource() }
}

val remoteDatasourceModule = module {
    single<Datasource> { RemoteDatasource() }
}
```

Then we just need to launch Koin with the right combination of modules:

```kotlin
// Load Repository + Local Datasource definitions
startKoin {
    modules(repositoryModule,localDatasourceModule)
}

// Load Repository + Remote Datasource definitions
startKoin {
    modules(repositoryModule,remoteDatasourceModule)
}
```


## Module Includes (since 3.2)

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

:::info
If you have any compiling issue while including modules from the same file, either use `get()` Kotlin attribute operator on your module or separate each module in files. See https://github.com/InsertKoinIO/koin/issues/1341 workaround
:::
