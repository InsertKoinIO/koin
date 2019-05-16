---
layout: docs
title: Koin for Android
description: Koin features to help Android development 
group: quick-references
toc: true
---

### Starting Koin for Android

In any Android class:

{% highlight kotlin %}
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(this@MainApplication)

            // load properties from assets/koin.properties file
            androidFileProperties()

            // module list
            modules(offlineWeatherApp)
        }
    }
}
{% endhighlight %}

<div class="alert alert-primary" role="alert">
    if you can't inject android context or appliation, be sure to use androidContext() function in your Koin application declaration.
</div>

### Use the Android context in your module

In your definition, you can inject `Context` or `Application` instance with `androidContext()` and `androidApplication()` functions:

{% highlight kotlin %}
module {
    single { MyAndroidComponent(androidContext()) }
}
{% endhighlight %}

### Android Components are KoinComponents

`Activity`, `Fragment` & `Service` are extendend by Koin to be considered as `KoinComponents` out of the box:

{% highlight kotlin %}
class MyActivity : AppCompatActivity(){

    // Inject MyPresenter
    val presenter : MyPresenter by inject()

    override fun onCreate() {
        super.onCreate()

        // or directly retrieve instance
        val presenter : MyPresenter = get()
    }
}
{% endhighlight %}

Those classes can then use:

* `get()` or `by inject()` instance retrieving
* `getKoin()` to access th current `Koin` instance

If you need to inject dependencies from another class and can't declare it in a module, you can still tag it with `KoinComponent` interface.

### Extended Scope API for Android (koin-android-scope or koin-androidx-scope projects)

Scope API is more close to the Android platform. Both `Activity` & `Fragment` have extensions for Scope API: `currentScope` get the current associated Koin scope. This scope is created & bound to the component's lifecycle.

You can use directly the associated Koin scope to retrieve components:

{% highlight kotlin %}
class DetailActivity : AppCompatActivity(), DetailContract.View {

    // inject from current activity scope instance
    override val presenter: DetailContract.Presenter by currentScope.inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        //...
    }
}
{% endhighlight %}

Easy to declare your Android component's scope:

{% highlight kotlin %}
module {
    // declare a scope for DetailActivity
    scope(named<DetailActivity>)> {
        scoped<DetailContract.Presenter> { DetailPresenter(get(), get()) }
    }
}
{% endhighlight %}

Any Activity or Fragment can use directly the scope API: `createScope()`, `getScope()` and `deleteScope()`.

### Android Architecture ViewModel (koin-android-viewmodel or koin-androidx-viewmodel projects)

Koin brings special features to manage ViewModel:

* `viewModel` special DSL keyword to declare a ViewModel
* `by viewModel()` & `getViewModel()` to inject ViewModel instance (from `Activity` & `Fragment`)
* `by sharedViewModel()` & `getSharedViewModel()` to reuse ViewModel instance from hosting Activity (from `Fragment`)

Let's declare a ViewModel in a module:

{% highlight kotlin %}
val myModule : Module = applicationContext {
    
    // ViewModel instance of MyViewModel
    // get() will resolve Repository instance
    viewModel { MyViewModel(get()) }

    // Single instance of Repository
    single<Repository> { MyRepository() }
}
{% endhighlight %}

Inject it in an Activity:

{% highlight kotlin %}
class MyActivity : AppCompatActivity(){

    // Lazy inject MyViewModel
    val model : MyViewModel by viewModel()

    override fun onCreate() {
        super.onCreate()

        // or also direct retrieve instance
        val model : MyViewModel = getViewModel()
    }
}
{% endhighlight %}

