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
module. Definitions are lazy, and then are resolved only when a a component is requesting it.

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
Koin does't have any import concept. Koin definitions are lazy: a Koin definition is started with Koin container but is not instantiated. An instance is created only a request for its type has been done.
:::

We just have to declare list of used modules when we start our Koin container:

```kotlin
// Start Koin with moduleA & moduleB
startKoin{
    modules(moduleA,moduleB)
}
```

Koin will then resolve dependencies from all given modules.

## Overriding definition or module (3.1.0+)

New Koin override strategy allow to override any definition by default. You don't need to specify `oevrride = true` anymore in your module.

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

This way, your share the definitions and avoid preallocate factories in a value.

## Overriding definition or module (before 3.1.0)

Koin won't allow you to redefinition an already existing definition (type,name,path ...). You will an an error if you try this:

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