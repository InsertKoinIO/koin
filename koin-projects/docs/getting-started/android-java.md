---
layout: docs
title: Getting started with Android & Java
description: Let's start with Koin on a simple Android app with some Java
group: getting-started
toc: true
---

## About

This tutorial lets you write an Android/Kotlin application and use Koin inject and retrieve your components.

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
    // Koin for Android
    compile 'org.koin:koin-android:{{ site.current_version }}'
}
{% endhighlight %}

## Our components (Java & Kotlin)

Let's create a HelloRepository to provide some data:

{% highlight kotlin %}
interface HelloRepository {
    fun giveHello(): String
}

class HelloRepositoryImpl() : HelloRepository {
    override fun giveHello() = "Hello Koin"
}
{% endhighlight %}

Let's create a Java presenter class, for consuming this data:

{% highlight java %}
public class MyJavaPresenter {

    private HelloRepository repository;

    public MyJavaPresenter(HelloRepository repository) {
        this.repository = repository;
    }

    public String sayHello(){
        String hello = repository.giveHello();

        return hello+" from "+this;
    }

}
{% endhighlight %}

## Writing the Koin module

Use the `module` function to declare a module. Let's declare our first component:

{% highlight kotlin %}
val appModule = module {

    // single instance of HelloRepository
    single<HelloRepository> { HelloRepositoryImpl() }

    // Simple Presenter Factory
    factory { MyJavaPresenter(get()) }
}
{% endhighlight %}

*Note:* we declare our MySimplePresenter class as `factory` to have a create a new instance each time our Activity will need one.

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

## Injecting dependencies into Java Activity

The `MyJavaPresenter` component will be created with `HelloRepository` instance. To get it into our Activity, let's inject it with the static `inject()` function: 

{% highlight kotlin %}
// import inject
import static org.koin.java.standalone.KoinJavaComponent.inject;

public class JavaActivity extends AppCompatActivity {

    private Lazy<MySimplePresenter> presenter = inject(MySimplePresenter.class);
    private Lazy<MyJavaPresenter> javaPresenter = inject(MyJavaPresenter.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        //...
    }
}
{% endhighlight %}

<div class="alert alert-primary" role="alert">
    The <b>inject()</b> function allows us to retrieve a lazy Koin instances, in Android components runtime (Activity, fragment, Service...)
</div>

<div class="alert alert-secondary" role="alert">
    The <b>get()</b> function is here to retrieve directly an instance (non lazy)
</div>


## What's Next?

You have finished this starting tutorial. Below are some topics for further reading:

* [Android Quick References]({{ site.baseurl }}/docs/{{ site.docs_version }}/quick-references/)

Also other Android getting started project:

* [Getting started Android & Scope feature]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/android-scope/)
* [Getting started Android & ViewModel feature]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/android-viewmodel/)
