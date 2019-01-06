---
layout: docs
title: koin-android
description: Koin for Android Developers
group: quick-references
toc: true
---

## Gradle Setup

Choose the needed koin-android dependency below:

{% highlight gradle %}
// Add Jcenter to your repositories if needed
repositories {
    jcenter()    
}
dependencies {
    // Koin for Android
    implementation 'org.koin:koin-android:{{ site.current_version }}'
    // Android Scope feature
    implementation 'org.koin:koin-android-scope:{{ site.current_version }}'
    // Android Scope feature - AndroidX
    implementation 'org.koin:koin-androidx-scope:{{ site.current_version }}'
    // Android Architecture ViewModel feature
    implementation 'org.koin:koin-android-viewmodel:{{ site.current_version }}'
    // Android Architecture ViewModel feature - AndroidX
    implementation 'org.koin:koin-androidx-viewmodel:{{ site.current_version }}'
}
{% endhighlight %}

### Declare a module

To declare a Koin module, we use the [Koin DSL]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-dsl/). Below is a short example:

{% highlight kotlin %}
val appModule = module {

    // Factory of MyPresenter - resolve Repository instance
    // produce a new instance on each demand
    factory<MyPresenter>()

    // Single instance of Repository
    single<Repository> { MyRepository() }
}
{% endhighlight %}

All components described in modules can/should be injected by constructor.

*NB*: you can get the Android `Context` instance by using `androidContext()` function.

### Start Koin

The `startKoin()` function is called from an `Application` class:

{% highlight kotlin %}
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // start Koin context
        startKoin(this, listOf(appModule)
    }
}
{% endhighlight %}

You can specify the following options at start:
* extraProperties - addtionnal properties
* loadProperties - use assets/koin.properties file
* logger - Koin logger (default is AndroidLogger)
* createOnStart - create tagged `createOnStart` instances at start (default is true)

If you want to start it elsewhere, just use the standard `StandAloneContext.startKoin()` or `loadKoinModules()` if you have already started it.

### Inject into Android Components

`Activity`, `Fragment` & `Service` are extendend by Koin to be considered as [KoinComponent]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/koin-core/#inject-with-koincomponents) out of the box:

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

If you need to inject dependencies from another class and can't declare it in a module, you can still tag it with `KoinComponent` interface.

### Using Scopes with Android

Extended scope feature for Android are available with `koin-android-scope` & `koin-androidx-scope`.

See Scope API section to better know about Scopes basics.

#### Binding scopes & lifecycle

Koin gives the `bindScope` function to bind the actual Android component lifecycle, to a given scope. On lifecycle's end, this will close the bound scope.

{% highlight kotlin %}
// A scoped Presenter
module {
    scope("session") { Presenter() }
}

// create "session" scope & inject it
class MyActivity : AppCompatActivity() {

    // inject Presenter instance, tied to current MyActivity's scope
    val presenter : Presenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // bind current lifecycle to Activity's scope
        bindScope(createScope("session"))
    }
{% endhighlight %}

#### Sharing an instance between components

In a more extended usage, you can use a `Scope` instance across components. For example, if we need to share a `UserSession` instance.

First declare a scope definition:

{% highlight kotlin %}
module {
    // Shared user session data
    scope("session") { UserSession() }
}
{% endhighlight %}

When needed to begin use a `UserSession` instance, create a scope for it:

{% highlight kotlin %}
getKoin().createScope("session")
{% endhighlight %}

Then use it anywhere you need it:

{% highlight kotlin %}
class MyActivity1 : AppCompatActivity() {

    // inject Presenter instance, tied to current MyActivity's scope
    val userSession : UserSession by inject()
}
class MyActivity2 : AppCompatActivity() {

    // inject Presenter instance, tied to current MyActivity's scope
    val userSession : UserSession by inject()
}
{% endhighlight %}

or you can also inject it with Koin DSL. If a presenter need it:

{% highlight kotlin %}
class Presenter(val userSession : UserSession)
{% endhighlight %}

Just inject it into constructor, with teh right scope:

{% highlight kotlin %}
module {
    // Shared user session data
    scope { UserSession() }
    // Inject UserSession instance from "session" Scope
    factory { Presenter(get())}
}
{% endhighlight %}

When you have to finish with your scope, just close it:

{% highlight kotlin %}
val session = getKoin().getScope("session")
session.close()
{% endhighlight %}

### Android Architecture ViewModel

With `koin-android-viewmodel` & `koin-androidx-viewmodel` projects, Koin brings special features to manage ViewModel:

* `viewModel` special DSL keyword to declare a ViewModel
* `by viewModel()` & `getViewModel()` to inject ViewModel instance (from `Activity` & `Fragment`)
* `by sharedViewModel()` & `getSharedViewModel()` to reuse ViewModel instance from hosting Activity (from `Fragment`)

Let's declare a ViewModel in a module:

{% highlight kotlin %}
val myModule : Module = applicationContext {
    
    // ViewModel instance of MyViewModel
    // get() will resolve Repository instance
    viewModel { MyViewModel(get()) }
    // or
    viewModel<MyViewModel>()

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

        // or direct retrieve instance
        val model : MyViewModel = getViewModel()
    }
}
{% endhighlight %}

_note_: `viewModel` keyword allow to use a declaration `viewModel { MyViewModel(get()) }` or to use directly the type to build `viewModel<MyViewModel>()`. This last is experimental and uses reflection to find the cosntructor and help you fill the right dependencies.

## More about Koin Android Features

* [Koin for Android developer documentation]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_koin_for_android_developers)
* [Alternate startKoin() in Android]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_starting_koin_with_android_context_from_elsewhere)
* [Scope features for Android]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_scope_features_for_android)
* [Using Android ViewModel]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_architecture_components_with_koin_viewmodel)
* [Shared ViewModel with Fragments]({{ site.baseurl }}/docs/{{ site.docs_version }}/documentation/reference/index.html#_shared_viewmodel)
