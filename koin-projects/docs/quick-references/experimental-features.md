---
layout: docs
title: Experimental Features
description: about external & incubation features
group: quick-references
toc: true
---

## Better definition declaration (Experimental)

Koin DSL can be seen as "manual", while you must fill constructors with "get()" function to resolve needed instances. When your definition don't need any special constructor integration (injection paarameters
or special scope Id), we can go with more compact writing style thanks to API below.

_note_: Using reflection is not costless. it replaces what you don"t want to write with reflection code (finding primary constructors, injecting parameters...). Mind it before using it, if you are on
performances constraints platform (Android for example)

### Instance builder with create()

The first introduced function is the `create()` function.

Instead of declaring a definition with instantiating its constructor and retrieving instances with get()

{% highlight kotlin %}
module {
    single { ComponentA(get() ...) }
}
{% endhighlight %}

You can use instead, the `create()` function to build an instance from its primary constructor, and fill the needed dependencies.

{% highlight kotlin %}
module {
    single { create<ComponentA>() }
}
{% endhighlight %}

### Simplified definitions

You can also use the more "compact" notation that will use the `create()` function. Just use the single function without any expression:

{% highlight kotlin %}
import org.koin.experimental.builder.*

module {
    single<ComponentA>()
}
{% endhighlight %}

If you have an implementation type and want to resolve with a target type, you can use the following `single` function:

{% highlight kotlin %}
import org.koin.experimental.builder.extended.*

module {
    singleBy<Interface,Implementation>()
}
{% endhighlight %}

Also works for factory & scope definition!

## Documentation reference

Below are some further readings:

* [Building instance with reflection]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/koin-core/index.html#_building_instances_with_reflection)
