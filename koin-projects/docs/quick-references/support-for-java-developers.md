---
layout: docs
title: Support for Java developers
description: Koin features for Java developers
group: quick-references
toc: true
---

Below is a short description of `koin-java` features. This project is a small subset of static utils to help java developers.

### Start Koin

Just use the `startKoin()` static function (with the static import to reduce the syntax):

{% highlight java %}
import static org.koin.core.context.GlobalContext.start;

// Build KoinApplication instance
// Builder API style
KoinApplication koinApp = KoinApplication.create()
                .printLogger()
                .modules(koinModule);

// Statr KoinApplication instance
start(koinApp);
{% endhighlight %}

You have access to the [same options]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-core/#start-koin) than the Kotlin `startKoin()`.

### Declare a Java friendly module in Kotlin

You need to tag your module variable with `@JvmField` to make it readable from Java world:

{% highlight kotlin %}
@JvmField
val koinModule = module {
    single { ComponentA() }
    single { ComponentB(get()) }
    single { ComponentC(get(), get()) }

    module("anotherModule") {
        single { ComponentD(get()) }
    }
}
{% endhighlight %}

### Inject with static helpers

The `KoinJavaComponent` class is a static helper that brings Koin powers to Java:

* `inject()` - lazy inject instance
* `get()` - retrieve instance
* `getKoin()` - get Koin context

{% highlight java %}
import static org.koin.java.standalone.KoinJavaComponent.*;

ComponentA a = get(ComponentA.class);
Lazy<ComponentA> lazy_a = inject(ComponentA.class);
{% endhighlight %}
