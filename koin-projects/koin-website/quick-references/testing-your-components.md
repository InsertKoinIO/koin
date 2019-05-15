---
layout: docs
title: Testing your components with Koin
description: 
group: quick-references
toc: true
---

The `koin-test` project brings you small but powerfull tools to test your Koin application.

### Getting your components in test

Just tag your test class with `KoinTest`, and you will be able to unlock `KoinComponent` & testing features:

* `by inject()` - lazy inject an instance
* `get()` - retrieve an instance

Given the definitions below:

{% highlight kotlin %}
val appModule = module {
    single { ComponentA() }
    //...
}
{% endhighlight %}

We can write the test below:

{% highlight kotlin %}
class MyTest : KoinTest {

    // Lazy inject property
    val componentA : ComponentA by inject()

    // use it in your tests :)
    @Test
    fun `make a test with Koin`() {
        startKoin { modules(appModule) }

        // use componentA here!
    }
}
{% endhighlight %}

### Mocking on the fly

Once you have tagged your class with `KoinTest` interface, you can use the `declareMock` fucntion to declare mocks & behavior on the fly:

{% highlight kotlin %}
class MyTest : KoinTest {

    val componentA : ComponentA by inject()

    @Test
    fun `declareMock with KoinTest`() {
        startKoin { modules(appModule) }

        declareMock<ComponentA> {
            // do your given behavior here
            given(this.sayHello()).willReturn("Hello mock")
        }
    }
}
{% endhighlight %}

### Checking your modules

You can check if your modules are good (all definitions are bounded) with the `checkModules` function on your `KoinApplication` instance:

{% highlight kotlin %}
class MyTest : KoinTest {
    
    val componentA : ComponentA by inject()

    @Test
    fun `checking modules`() {
        // use koinApplication instead of startKoin, to avoid having to close Koin after each test
        koinApplication { modules(appModule) }.checkModules()
    }
}
{% endhighlight %}

### Starting & stopping your Koin instance

Take attention to stop your koin instance (if you use `startKoin` in your tests) between every test. Else be sure to use `koinApplication`, for local koin instances or `stopKoin()` to stop the current global instance.

