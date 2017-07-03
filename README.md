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
	3. [Type Binding](#type-binding)
	4. [Injecting a dependency](#injecting-a-dependency)
	5. [Start your context](#start-your-context)
	6. [Safely resolving a dependency](#safely-resolving-a-dependency)
	8. [Scopes](#scopes)
	8. [Loading mulitple modules](#loading-mulitple-modules)
	9. [Compile time & Lazy linking](#compile-time-lazy-linking)
	10. [Using properties](#using-properties)
4. [Library extensions](#library-extensions)
	1. [Koin for Android](#koin-for-android) 
5. [Roadmap](#roadmap)

## Setup

Check that you have `jcenter` repository and add the following gradle dependency:

### Gradle

```gradle
compile 'org.koin:koin-core:0.2.0'
```

### Maven

```xml
<dependency>
    <groupId>org.koin</groupId>
    <artifactId>koin-core</artifactId>
    <version>0.2.0</version>
</dependency>
```

## Getting Started

First of all, you need to write a **Module** to gather your components definitions. Then you will be ready to load and use it. Keep in mind, that **injection by constructor** is the default strategy targeted by Koin.
### Write your Module

Write a class that extends [Module]() class. Check the [example]() below :

```Kotlin
class MyModule : Module() {
    override fun context() =
        declareContext {
            provide { ServiceB() }
            provide { ServiceA(get()) }
        }
    }
}
```
Your context is provided by the `context()` function, and described via the `declareContext` function. This unlock the Koin DSL:

* `provide { /* component definition */ }` declare your component  
* `bind {/* compatible type */}` allowed bounded `Class` type to given *provided definition*
* `get()` resolve the component dependency
* `scope {/* scope class */}` creates a scope for all definitions in current module

### Setup your application

*Build* your module with the `Koin` builder class: 

```Kotlin
val myContext = Koin().build(MyModule())
```

This will return a `KoinContext` object, which manage your components instances.

Once you have built your context you can retrieve your components with `get()` function:

```Kotlin
val serviceA = myContext.get<ServiceA>()
//Do something with it ...
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
    override fun context() = 
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
	val context = Koin().build(SampleModule())
	// get your components ...
	val serviceA = context.get<serviceA>()
	// just run it
	serviceA.doSomethingWithB()
}
```

[Complete sample]()


## Using KOIN Framework

### Creating a module

Write a class that extends [Module]() class. Open a declaration section wihtin the `declareContext` function, for the `context()` method: 

```Kotlin
class MyModule : Module() {
    override fun context() =
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

### Type Binding

Once you have declare a component, you can specify additional type that can be used to instantiate it with `bind {}`. For example:

```kotlin
// interface
interface DoSomething {
    fun doSomething()
}
// component with interface
class ServiceB() : DoSomething {//...}

//in a module context
declareContext {
    // definie ServiceB and allow binding with DoSomething
    provide { ServiceB()} bind { DoSomething::class }
}
```
This way, we provide a component that can be bound to ServiceB::class &  DoSomething::class.


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

You can start one or several modules, with the Koin builder API `Koin().build( ** module instances **)`:

```kotlin
val context = Koin().build(MyModule())
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
val ctx = Koin().build(MyModule())

// will throw NoBeanDefFoundException if not found
val serviceA = ctx.get<ServiceA>()

// will return null if not found
val serviceA = ctx.getOrNull<ServiceA>() 
```

### Scopes

**A scope is an isolated space**, where you gather components instances. **By default, all components are setup at root scope**. Each component will be resolved against its scope. Let's take an example:

```kotlin
class SampleModuleB : Module() {
    override fun context() =
            declareContext {
                provide { ServiceB() }
            }
}

class ScopedModuleA : Module() {
    override fun context() =
            declareContext {
            	   // declare ServiceA scope
                scope { ServiceA::class }
                provide { ServiceA(get()) }
            }
}

// run context
val context = Koin().build(SampleModuleB(), ScopedModuleA())
```

The context will contains 2 scopes:

* Root scope : ServiceB instance
* ServiceA scope : ServiceA instance

When you retrieve `ServiceA` component, you will resolve `ServiceB` in **root scope**. Each time you retrieve `ServiceA`, you will resolve `ServiceA` from **ServiceA scope**.

The scope idea is to allow handle/differentiate module lifecycles & instances. You can release an entire scope:

```Kotlin
// release all instances from ServiceA scope
context.release(ServiceA::class)
```

### Loading mulitple modules

You can load several modules, into in your context like below:

```kotlin
class ModuleB : Module() {
    override fun context() =
        declareContext {
            provide { ServiceB() }
        }
    }
}

class ModuleA : Module() {
    override fun context() =
        declareContext {
   			  // Resolve ServiceB
            provide { ServiceA(get()) }
        }
    }
}

// load them all
val context = Koin().build(ModuleB(), ModuleA())
```

Resulting context will contain components from ModuleB and ModuleA.

### Compile time & Lazy linking

All your definitions are checked at compile time (your code is compiling), and the linking with `get()` is resolved lazily.

The `get()` function ask the container for given definition. But all of this done lazily, when you ask the dependency. That means that your can provide later definition:

```kotlin
// used classes
class ServiceB() {//...}
class ServiceA(val serviceB: ServiceB) {//...}

class MyModule : Module() {
    override fun context() {
        declareContext {
            // will resolve for ServiceB
            provide { ServiceA(get()) }
        }
    }
}
```

From here you can:

1. load another module with it, at start. Check [Loading mulitple modules](#loading-mulitple-modules) section
2. provide later, a definition on context


In case 2, provide a late definition:

```kotlin
val ctx = Koin().build(SampleModule())
// Provide a definition for ServiceB
ctx.provide { ServiceB() }
```

### Using properties

You can **inject properties into your context**, directly at start with a map of values via `properties()` function:

```kotlin
val ctx = Koin()
        .properties(mapOf("myVal" to "VALUE!"))
        .build(SampleModule())
```

And retrieve your value from context, with `getProperty()`:

```kotlin
val myVal = ctx.getProperty<String>("myVal")
```

In module definition, this will be done lazily:

```kotlin
class ServiceD(val myVal :String){//...}

class SampleModule : Module() {
    override fun context() =
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


