---
layout: docs
title: Getting started with Android & Scope feature
description: Let's start with Koin on a Android app with scoping feature
group: getting-started
toc: true
---

## About

This tutorial lets you write an Android/Kotlin application and use Koin inject and retrieve your components with scope.

## Get the code

<div class="container">
  <div class="row">
    <div class="col-8">
      Checkout the project directly on Github or download the zip file
    </div>
    <div class="col">
      <a href="https://github.com/InsertKoinIO/getting-started-koin-android" class="btn btn-outline-primary mb-3 mb-md-0 mr-md-3">Go to Github</a>
      <a href="https://github.com/InsertKoinIO/getting-started-koin-android/archive/master.zip" class="btn btn-outline-info mb-3 mb-md-0 mr-md-3">Download Zip</a>
    </div>
  </div>
</div>

## Gradle Setup

Add the Koin Android dependency like below:

{% highlight gradle %}
// Add Jcenter to your repositories if needed
repositories {
    jcenter()    
}
dependencies {
    // Koin for Android - Scope feature
    // include koin-android
    compile 'org.koin:koin-android-scope:{{ site.current_version }}'
}
{% endhighlight %}

## Our components

Let's create a HelloRepository to provide some data:

{% highlight kotlin %}
interface HelloRepository {
    fun giveHello(): String
}

class HelloRepositoryImpl() : HelloRepository {
    override fun giveHello() = "Hello Koin"
}
{% endhighlight %}

Let's create a presenter class, for consuming this data:

{% highlight kotlin %}
class MyScopePresenter(val repo: HelloRepository) {

    fun sayHello() = "${repo.giveHello()} from $this"
}
{% endhighlight %}

## Writing the Koin module

Use the `module` function to declare a module. Let's declare our first component:

{% highlight kotlin %}
val appModule = module {

    // single instance of HelloRepository
    single<HelloRepository> { HelloRepositoryImpl() }

    // Scoped MyScopePresenter instance
    scope(named<MyScopeActivity>()) {
        scoped { MyScopePresenter(get()) }
    }
}
{% endhighlight %}

*Note:* we declare our MyScopePresenter class as a `scoped` definition for `MyScopeActivity`. This will allows us to bind a MyScopePresenter with a scope, and drop this instance with the scope closing.

## Start Koin

Now that we have a module, let's start it with Koin. Open your application class, or make one (don't forget to declare it in your manifest.xml). Just call the `startKoin()` function:

{% highlight kotlin %}
class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }
}
{% endhighlight %}

## Injecting dependencies

The `MyScopePresenter` component will be created with `HelloRepository` instance. To get it into our Activity, let's inject it with the `by inject()` delegate injector: 

{% highlight kotlin %}
class MyScopeActivity : AppCompatActivity() {

    // inject MyScopePresenter from current scope 
    val scopePresenter: MyScopePresenter by currentScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

    }
}

{% endhighlight %}

<div class="alert alert-primary" role="alert">
    The <b>currentScope</b> allows us to retrieve/create a Koin scope for given activity
</div>

## What's Next?

You have finished this starting tutorial. Below are some topics for further reading:

* [Android Quick References]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/)

Also other Android getting started project:

* [Getting started Android]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/android/)
* [Getting started Android & ViewModel feature]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/android-viewmodel/)

