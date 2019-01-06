---
layout: docs
title: koin-test
description: Testing with Koin
group: quick-references
toc: true
---

Easy and straight forward features to test your app with Koin.

### Setup

First, check that the `koin-test` dependency is added like below:

{% highlight gradle %}
// Add Jcenter to your repositories if needed
repositories {
    jcenter()    
}
dependencies {
    
    testImplementation 'org.koin:koin-test:{{ site.current_version }}'
}
{% endhighlight %}

### Bring Koin powers to your test class

Just tag your test class with `KoinTest`, and you will be able to unlock features:

* `by inject()` - lazy inject an instance
* `get()` - retrieve an instance
* `declareMock()` - declare a mock (mockito) for given type
* `declare { }` - declare a definition on the fly

Given classes and module:

{% highlight kotlin %}
class ComponentA
class ComponentB(val a: ComponentA)

val appModule = module {
        single { ComponentA() }
        single { ComponentB(get()) }
    }
{% endhighlight %}

We can write the test below:

{% highlight kotlin %}
class MyTest : KoinTest {

    // Lazy inject property
    val componentB : ComponentB by inject()

    @Test
    fun `should inject my components`() {
        startKoin(listOf(appModule)))

        // directly request an instance
        val componentA = get<ComponentA>()

        assertNotNull(a)
        assertEquals(componentA, componentB.a)
    }
}
{% endhighlight %}

Note that you can use the standard `startKoin()` function to start Koin from your test. Don't forget to close it between tests.

### Checking your modules

You can check if your modules are good (all definitions are bounded):

{% highlight kotlin %}
    @Test
    fun `check module`() {
        checkModules(listOf(appModule)))
    }
{% endhighlight %}

## More about testing with Koin

* [More about testing with Koin]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_testing_with_koin)
* [Mocking out of the box]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_mocking_out_of_the_box)
* [Declare on the fly]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_declaring_a_component_on_the_fly)
* [Overriding definitions]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_overriding_a_definition_or_a_module)
* [Checking your modules configuration with check() and dryRun()]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/koin-core/index.html#_checking_your_koin_configuration)