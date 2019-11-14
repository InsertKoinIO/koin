---
layout: docs
title: Setup Koin
description: Pick your Koin dependency!
group: setup
toc: true
---

### JCenter Repository

{% highlight gradle %}
// Add Jcenter to your repositories if needed
repositories {
	jcenter()    
}
{% endhighlight %}

### Koin Core Features

{% highlight gradle %}
// Koin for Kotlin
compile "org.koin:koin-core:{{ site.current_version }}"
// Koin Extended & experimental features
compile "org.koin:koin-core-ext:{{ site.current_version }}"
// Koin for Java developers
compile "org.koin:koin-java:{{ site.current_version }}"
// Koin for Unit tests
testCompile "org.koin:koin-test:{{ site.current_version }}"
{% endhighlight %}

### Koin for Android

{% highlight gradle %}
// Koin for Android
compile "org.koin:koin-android:{{ site.current_version }}"
// Koin Android Scope feature
compile "org.koin:koin-android-scope:{{ site.current_version }}"
// Koin Android ViewModel feature
compile "org.koin:koin-android-viewmodel:{{ site.current_version }}"
{% endhighlight %}

### Koin for AndroidX

{% highlight gradle %}
// AndroidX (based on koin-android)
// Koin AndroidX Scope feature
compile "org.koin:koin-androidx-scope:{{ site.current_version }}"
// Koin AndroidX ViewModel feature
compile "org.koin:koin-androidx-viewmodel:{{ site.current_version }}"
{% endhighlight %}

### Koin for Ktor

{% highlight gradle %}
// Koin for Ktor Kotlin
compile "org.koin:koin-ktor:{{ site.current_version }}"
{% endhighlight %}