# Insert Koin to make dependency injection

KOIN is a dependency injection framework that uses Kotlin and its functional power to get things done!  No proxy/CGLib, No code generation, No introspection. Just functional Kotlin and DSL magic ;)

![logo](./img/insert_koin_logo.jpg)

# Table of Contents

1. [Setup](#Setup)
2. [Getting Started](#Getting_Started)
	1. [Write your Module](#Write_your_Module)
	2. [Setup your application](#Setup_your_application)
2. [A first Example](#a-first-example)
3. [Using KOIN Framework](#using-koin-framework)
	1. [Creating a module](#creating-a-module)
	2. [Providing a component](#providing-a-component)
	3. [Injecting a dependency](#injecting-a-dependency)
	4. [Start your context](#start-your-context)
	5. [Safely resolving a dependency](#safely-resolving-a-dependency)
	6. [Factory components](#factory-components)
	7. [Stacking components](#stacking-components)
	8. [Deleting & removing](#deleting-&-removing)
	9. [Importing modules](#importing-modules)
	10. [Lazy linking](#lazy-linking)
	11. [Injecting with @Inject](#injecting-with-@inject)
	12. [Using properties](#using-properties)
4. [Library extensions](#library-extensions)
	1. [Koin for Android](#koin-for-android) 
5. [Roadmap](#roadmap)

## Setup

Check that you have `jcenter` repository and add the following gradle dependency:

### Gradle

```gradle
compile 'org.koin:koin-android:0.1.2'

```

### Maven

```xml
<dependency>
    <groupId>org.koin</groupId>
    <artifactId>koin-core</artifactId>
    <version>0.1.2</version>
</dependency>
```

## Getting Started

First of all, you need to write a **Module** to gather your components definitions. Then you will be ready to load your module and inject yoru components. Keep in mind, that **injection by constructor** is the default strategy targeted by Koin. Write your components with constructors for your dependencies.

### Write your Module

Write a class that extends [Module](https://github.com/Ekito/koin/blob/master/src/main/kotlin/org/koin/module/Module.kt) class. Check the [example](https://github.com/Ekito/koin/blob/master/src/test/kotlin/org/koin/test/koin/SimpleKoin.kt) below :

```Kotlin
class MyModule : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceB() }
            provide { ServiceA(get()) }
        }
    }
}

```
Open declaration section wihtin the `declareContext` function, in the in the `onLoad()` method.  The `declareContext` function bring you the Koin DSL and allow you to declare your components.

* `provide {}` function helps you to declare your component within function Each lambda passed to this function must return an expression for your component creation. e.g: class instance. 
* `get()` function allow you to resolve the given dependency and retrieve your component instance


### Setup your application

*Build* your module with the `Koin` builder class: 

```Kotlin
val myContext = Koin().build(MyModule::class)
```

This will return a **Koin context** on which you will get your components instances.

Once you have your Koin context you can get your components with `get()` function:

```Kotlin
val ctx = Koin().build(MyModule::class)

val serviceA = ctx.get<ServiceA>()
serviceA.doSomethingWithB()
```

## A first Example

Let's take the following Kotlin classes. We use injection by constructor:

```kotlin
class ServiceB() {//...}
class ServiceA(val serviceB: ServiceB) {//...}
```

Let's create a module to declare our components:

```kotlin
class SampleModule : Module() {
    override fun onLoad() {
        declareContext {
            // Provide ServiceB
            provide { ServiceB() }
            // Provide ServiceA and resolve ServiceB with get()
            provide { ServiceA(get())}
        }
    }
}
```

**Note:** Dependency resolution is made lazily

Start a Kotlin app and run it:

```kotlin
fun main(args: Array<String>) {
	val context = Koin().build(SampleModule::class)
	// get your components ...
	val serviceA = context.get<serviceA>()
	// run it
	serviceA.doSomethingWithB()
}
```

[Complete sample here](https://github.com/Ekito/koin/blob/master/src/test/kotlin/org/koin/test/koin/SimpleKoin.kt)


## Using KOIN Framework

### Creating a module

Write a class that extends [Module](https://github.com/Ekito/koin/blob/master/src/main/kotlin/org/koin/module/Module.kt) class. Open a declaration section wihtin the `declareContext` function, in the `onLoad()` method: 

```Kotlin
class MyModule : Module() {
    override fun onLoad() {
        declareContext {
            // your decleration here...
        }
    }
}

```
The `declareContext` function brings you the Koin context and its DSL to work with your dependencies. All the features below can be used here, or on a built context from **Koin builder**.


### Providing a component 

Whitin `declareContext` function or on a Koin context object, you can use the `provide` function for functional declaration (lambda containing your component instantiation). This will provide a **singleton** instance for your component.

```kotlin
declareContext {
    // provide Singleton definition for ServiceB
    provide { ServiceB()}
}
```

You can also provide a component by its class, and let Koin makes **introspection** to find its constructor:

```kotlin
declareContext {
    provide(ServiceB::class)
}
```
In this case, **all dependencies will be lazily injected like in functional declaration**. This will be also a singleton instance.


### Injecting a dependency

In a Koin module, you can resolve an component instance with the `get()` function:

```kotlin
// used classes
class ServiceB() {//...}
class ServiceA(val serviceB: ServiceB) {//...}

//in a module context
declareContext {
    // Resolving needed dependency with get()
    provide { ServiceA(get())}
}
```

Instance resolution will be made lazily: instance will be created/resolve when you will ask for it.


### Start your context

You can start one or several modules, with the Koin builder API `Koin().build(...)`:

```kotlin
val context = Koin().build(MyModule::class)
```

Now you can retrieve your components instances, everywhere you have your context:

```kotlin
// Retrieve serviceA component
val serviceA = context.get<ServiceA>()
```

**Note:** : Resolving component dependencies in constructor don't require to specify type, because constructors already hold it.  

### Safely resolving a dependency

If you are not sure about a dependency, you can resolve it safely with `getOrNull()`:

```kotlin
val ctx = Koin().build(MyModule::class)

// will throw NoBeanDefFoundException if not found
val serviceA = ctx.get<ServiceA>()

// will return null if not found
val serviceA = ctx.getOrNull<ServiceA>() 
```

### Factory components

On a context or while declaring a module, instated of providing a singleton you can provide a factory with `factory` function: 

```Kotlin
declareContext {
	factory { ServiceB() }
}
```
```Kotlin
// Resolve isntances from ServiceA
val serviceA_1 = ctx.get<ServiceA>()
val serviceA_2 = ctx.get<ServiceA>()

// serviceA_1 & serviceA_2 are different !
```

Koin will **create a new instance for each demand**. Beware that Koin do not retain previous created instance.

### Stacking components

You can also provide a component with **one use only** lifecycle. Component is put on stack: 

```Kotlin
declareContext {
	stack { ServiceB() }
}
```

When you will request ServiceB component, Koin will **create an instance and remove it all** from container. You won't be able to retrieve it anymore after.

```kotlin
class SampleModule : Module() {
    override fun onLoad() {
        declareContext {
            stack { ServiceB() }
        }
    }
}
```
```Kotlin
// Start module
val context = Koin().build(SampleModule::class)
// ServiceB is retrieved
val first = context.get<ServiceB>()
// ServiceB is gone
val second = context.getOrNull<ServiceB>()
```

### Deleting & removing

On a Koin context, you can delete an instance via its class (further instance access will recreate an instance) with `delete`:

```kotlin
ctx.delete(ServiceB::class)
```

You can also remove instance and its definition (no more instance can be created) with `remove`:

```kotlin
ctx.remove(ServiceB::class)
```

### Importing modules

You can import several modules definitions, into in your actual module with `import` function:

```kotlin
class ModuleB : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceB() }
        }
    }
}

class ModuleImportingB : Module() {
    override fun onLoad() {
        declareContext {
            // Import module containg ServiceB definition
            import(SampleModuleB::class)
   			  // Resolve ServiceB
            provide { ServiceA(get()) }
        }
    }
}
```

When loading module `Koin().build(ModuleImportingB::class)`, resulting context will contain components from SampleModuleB and ModuleImportingB.

### Lazy linking

You must provide a compiling bean definition with `get()`:

```Kotlin
provide { ServiceA(get()) }
```

The `get()` ask the container for given definition. But all of this done lazily, when you ask the dependency. That mean that your can provide later definition:

```kotlin
// used classes
class ServiceB() {//...}
class ServiceA(val serviceB: ServiceB) {//...}

class MyModule : Module() {
    override fun onLoad() {
        declareContext {
            // will resolve for ServiceB
            provide { ServiceA(get()) }
        }
    }
}
```

From here you can:

1. make direct module import in your module
2. load another module with it, at start
3. provide a definition on context

In case 2, load a 2nd module at start:

```kotlin
//load 2 modules
val ctx = Koin().build(SampleModule::class, <ModuleWithServiceB>::class)
```

In case 3, provide a late definition:

```kotlin
val ctx = Koin().build(SampleModule::class)
// Provide a definition for ServiceB
ctx.provide { ServiceB() }
```

### Injecting with @Inject

For those who still want to use `@Inject` in their classes, you can ask Koin to inject dependencies on a given object intance, with the `inject` function from a Koin context. Take the following class:

```kotlin
class MyClass {
    @Inject
    lateinit var serviceB: ServiceB
}
```

And inject needed dependencies:

```kotlin
val ctx = Koin().build(SampleModule::class)
// create your class isntance
val myClass = MyClass()
// inject it with koin
ctx.inject(myClass)
// run it !
myClass.serviceB.doSomething()
```

### Using properties

You can **inject properties into your context**, directly at start with a map of values via `properties()` function:

```kotlin
val ctx = Koin()
        .properties(mapOf("myVal" to "VALUE!"))
        .build(SampleModule::class)
```

And retrieve your value from context, with `getProperty()`:

```kotlin
val myVal = ctx.getProperty<String>("myVal")
```

In module definition, this will be done lazily:

```kotlin
class ServiceD(val myVal :String){//...}

class SampleModule : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceD(getProperty<String>("myVal"))}
        }
    }
}
```

You can **resolve safely a property** with `getPropertyOrNull()`, which lets you retrieve a nullable property.

To set a property on a context, use the `setProperty(key,value)` function.

## Library extensions

### [Koin for Android](https://github.com/Ekito/koin-android)

The Android dedicated Koin library to help you make dependency injection on Android

## Roadmap

* Javadoc/Dokka Documentation
* Lifecycle & Architecture
* get/set component with qualifiers


