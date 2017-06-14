# KOIN - KOtlin dependency INjection framework

KOIN is a simple dependency injection framework that use Kotlin and its functional power to get it done!  No Proxy, No code generation, No introspection. Just functional Kotlin and DSL ;)

**Insert Koin and start injecting :)**

## Gradle Setup

Check that you have jcenter repository and add the following gradle dependency:

```gradle
// if not already in your gradle files
repositories {
        jcenter()
}

compile 'org.koin:koin-android:0.1.0'

```

## Getting Started

First of all, you need to write a **module** to gather your components definitions.

### Writing your Module

Write a class that extends [Module](https://github.com/Ekito/koin/blob/master/src/main/kotlin/org/koin/module/Module.kt) class. Check the [example](https://github.com/Ekito/koin/blob/master/src/test/kotlin/org/koin/test/koin/SimpleKoin.kt) below :

```Kotlin
class MyModule : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceB() }
            provide { ServiceA(get()) }
            provide { ServiceC(get(),get()) }
        }
        //...
    }
}

```
Your must open declaration section in the `onLoad()` method, with the `declareContext` function.  

Each `provide` function helps you to declare your component. Each lambda passed to this function must return an expression for your component creation. e.g: class instance. 

You can use the `get()` method to inject your component constructor, which will resolve the needed dependency.

### Setup your Kotlin Application

To start your module, you just need to build it: 

```Kotlin
val myContext = Koin().build(MyModule::class)
```

This will return a context on which you will get your components instances.

### Just get Koin

Once you have your Koin context you can get your components with `get()`:

```Kotlin
val ctx = Koin().build(MyModule::class)

val serviceA = ctx.get<ServiceA>()
serviceA.doSomethingWithB()

val serviceC = ctx.get<ServiceC>()
serviceC.doSomethingWithAll()

val serviceB = ctx.get<ServiceB>()
```

## Running Example

### A first Example

Let's take the following Kotlin classes:

```kotlin
class ServiceB() {

    init {
        println("$this is B")
    }

    fun doSomething(){
        println("$this do something in B")
    }
}
class ServiceA(val serviceB: ServiceB) {
    init {
        println("$this is A")
    }

    fun doSomethingWithB(){
        println("$this do something in A")
        serviceB.doSomething()
    }
}
```

Let's create a module to declare our components:

```kotlin
class SampleModule : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceB() }
            // ServiceB will be injected into ServiceA
            provide { ServiceA(get())}
        }
    }
}
```

**note:** we only need to use **get()** in ServiceA constructor, it will retrieve the given dependency.

Start a Kotlin app and run your context:

```kotlin
fun main(args: Array<String>) {
	val context = Koin().build(SampleModule::class)

	val serviceA = context.get<serviceA>()
	val serviceB = context.get<ServiceB>()

	serviceA.doSomethingWithB()
}
```

[Check the sample](https://github.com/Ekito/koin/blob/master/src/test/kotlin/org/koin/test/koin/SimpleKoin.kt)


## Advanced features

### Safely Get Components

You can safely retrieve an optional component with `getOtNull` function like:

```kotlin
val serviceB : ServiceB? = context.getOrNull()
```

### Module Import

You can make imports from existing modules, in your module:

```kotlin
class SampleModuleB : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceB() }
        }
    }
}

class SampleModuleAAndC : Module() {
    override fun onLoad() {
        declareContext {
            import(SampleModuleB::class)

            provide { ServiceA(get()) }

            provide { ServiceC(get(), get()) }
        }
    }
}
```

When loading module `Koin().build(SampleModuleAAndC::class)`, resulting context will contain components from SampleModuleB and SampleModuleAAndC.


### Providing a class

When declaring a module, you can use `provide` with functional declaration (lambda containing your component instantiation). Or you can use directly the class like: `provide (ServiceA::class))`. This will look up for the first class constructor, and fill definition.

We can write SampleModuleAAndC as follow:

```kotlin
class SampleModuleAAndC : Module() {
    override fun onLoad() {
        declareContext {
            provide(ServiceA::class)
            provide(ServiceC::class)
        }
    }
}
```
[Check test sample](https://github.com/Ekito/koin/blob/master/LICENSE)

**note:** it will take more resources & time than declaring directly with lambda form (Koin will write it for you, but it has a price)

### Factory Beans

On a context or while declaring a module, you can provide a component as a factory with `factory { ServiceB() }`. Each time you will request ServiceB component, Koin will create a new instance. 

### Stacking

On a context or while declaring a module, you can provide a component as stacked with `stack { ServiceB() }`. This is for *"one use only"* component. Next time you will request ServiceB component, Koin will create an instance and remove its definition from container. You won't be able to retrieve it after.

_**Example:**_

```kotlin
class SampleModule : Module() {
    override fun onLoad() {
        declareContext {
            stack { ServiceB() }
        }
    }
}
```

```kotlin
val context = Koin().build(SampleModule::class)

// is retrieved
val first = context.get<ServiceB>()
// is null
val second = context.getOrNull<ServiceB>()
```

### @Inject

For those who still want to use @Inject in their classes, you can ask Koin to inject dependencies on a given object intance, with the `inject` function from a Koin context. 
Take the following class:

```kotlin
class MyClass {
    @Inject
    lateinit var serviceB: ServiceB
}
```
And inject needed depndencies:

```kotlin
val ctx = Koin().build(SampleModule::class)
val myClass = MyClass()
ctx.inject(myClass)
myClass.serviceB.doSomething()
```

### Properties

You can inject properties into your context, directly at start (map of values):

```kotlin
val ctx = Koin()
        .properties(mapOf("myVal" to "VALUE!"))
        .build(SampleModule::class)
```

And retrieve your value from context:

```kotlin
val myVal = ctx.getProperty<String>("myVal")
```
or in your module:

```kotlin
class SampleModuleD : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceD(getProperty<String>("myVal"))}
        }
    }
}
```

`getPropertyOrNull` let you retrieve a nullable property.

`setProperty(key,value)` is also available to declare a property.

## More ...

### [Koin for Android](https://github.com/Ekito/koin-android)


## TODO / Roadmap

* Qualifiers
* Better Documentation page ;)


