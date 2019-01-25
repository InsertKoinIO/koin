---
layout: docs
title: Upgrading from Koin 0.9.x
description: How to upgrade your code from older version
group: quick-references
toc: true
---

## Changes in DSL

The following functions & keywords have been renamed:

* `applicationContext` -> `module` (declaring a module)
* `context` -> `module` (declaring a submodule)
* `bean` -> `single` (unique instance definition)
* `controller` need your class to be tagged as `SparkController`

Some core functions that have been renamed:
- `releaseContext` -> `release`
- `closeKoin` -> `stopKoin`

## Explicit overriding

Now you have to specify that a definition or a module will override any existing definition of the same type: [overriding definition or module]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_overriding_a_definition_or_a_module)

{% highlight kotlin %}
val myModuleA = module {

    single { ServiceImp() as Service }
}

val myModuleB = module {

    // override for this definition
    single(override=true) { TestServiceImp() as Service }
}

// Allow override for all definitions from module
val myModuleB = module(override=true) {

    single { TestServiceImp() as Service }
}
{% endhighlight %}

## Implicit naming

If you don"t give a name to your definition, Koin will give one, dependeing on class name & path:

{% highlight kotlin %}
module {
    module("B") {
        single { ComponentA() }
        single { ComponentB(get()) }
    }

    module("C") {
        single { ComponentA() }
        single { ComponentC(get()) }
    }
}
{% endhighlight %}

you can resolve ComponentA with `get(name="B.ComponentA")` or `get(name="C.ComponentA")`.


## From module release to Scope APi 

Previous Scopeing API was very confusing. it was mixing namespace modules with scoping capability. In Koin 1.0 we have clearly seperated the 2 aspects: module for namepsace & scope for limited lifetime instances. 

In `0.9.x`, moduels also served as pseudo scope support. 

{% highlight kotlin %}
module {
    module("B") {
        single { ComponentA() }
        single { ComponentB(get()) }
    }
}
{% endhighlight %}

Then you could do `release("B")` to release instances from module "B".

{% highlight kotlin %}
val a : ComponentA = get()
val b : ComponentB = get()
release("B") // drop A & B instances
{% endhighlight %}

In Koin `1.0`, use the `scope` definitions to define components that will be bound to a scope (Others that don"t have to be dropped, can stay with single).

{% highlight kotlin %}
module {
    module("B") {
        scope("session") { ComponentA() }
        scope("session") { ComponentB(get()) }
    }
}
{% endhighlight %}

Use the Scope APi with a scope to handle your scoped instances:

{% highlight kotlin %}
val session = getKoin().createScope("session")
val a : ComponentA = get()
val b : ComponentB = get()
session.close() // drop A & B instances
{% endhighlight %}

## Injection Parameters 

Injection parameters have been reviewed, check the following documentation: [injection parameters]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_injection_parameters) 

{% highlight kotlin %}
val myModule = module {
    single{ view : View -> Presenter(view) }
}

class MyComponent : View, KoinComponent {

    // inject this as View value
    val presenter : Presenter by inject { parametersOf(this) }
}
{% endhighlight %}

## Moving from koin-android-architecture & koin-androidx

For a better readability of features projects, we have decided to deprecate `koin-android-architecture` & `koin-androidx` gradle artifacts. In Koin 1.0, you won't find those projects but rather those new ones: `koin-android-viewmodel` & `koin-androidx-viewmodel`.

Gradle projects have been moved like follow:
- `koin-android-architecture` -> `koin-android-viewmodel`
- `koin-androidx` -> `koin-androidx-viewmodel`

You have to fix your imports on your source code with new packages:

{% highlight kotlin %}
// koin-android-viewmodel
org.koin.android.viewmodel.ext.android.*
// koin-androidx-viewmodel
org.koin.androidx.viewmodel.ext.android.*
{% endhighlight %}
