---
layout: docs
title: Android Developer Guide
description: Developer guides & receipes for Android
group: developer-guides
toc: true
---

## Android & Koin Best practices

Here you will find some receipes and guide to build your Android application.

### How to build MVP

To illustrate the eaxmples below, we will take a small usecase: a view that has to display weather detail.

A very good explaination article is one from Florina Muntenescu: [Android Architecture Patterns Part 2:
Model-View-Presenter](https://medium.com/upday-devs/android-architecture-patterns-part-2-model-view-presenter-8a6faaae14a5)

#### The Contract: linking the View & Presenter

In MVP, we begin by the contract: a View to show the detail, a presenter to get the detail.

{% highlight kotlin %}
interface DetailContract {
    interface View {
        fun showDetail(weather: DailyForecastModel)
        fun showError(error : Exception)
    }

    interface Presenter {
        fun getDetail()
    }
}
{% endhighlight %}

#### The Presenter

Let's write a Presenter, a component dedicated to present the weather data. we need a dependency, injected by constructor: `val weatherRepository: WeatherRepository`. This will help us request weather data. 

The presenter will be attached to a view and detached from this view at the end of the process. That's why we have `subscribe` and `unsubscribe` functions. The `getDetail` function get the data, and ask the View to display either result or error.

{% highlight kotlin %}
class DetailPresenter(
    private val weatherRepository: WeatherRepository
) : DetailContract.Presenter {

    var view : DetailContract.View?
    
    fun subscribe(v: DetailContract.Presenter) {
        view = v
    }

    fun unSubscribe() {
        view = null
    }

    override fun getDetail(id: String) {
        // ask for detail in background
        val detail = weatherRepository.getWeatherDetail(id)

        // deals with result
        if (detail != null) {
            // show result
            view?.showDetail(detail)
        }
        else {
            // show error
            view?.showError(error)
        }
    }
}
{% endhighlight %}

#### The View

Let's use the Presenter in our View. We just need to inject our Presenter with `val presenter: DetailContract.Presenter by inject()`. 

Then we have to subscribe to our Presenter, and ask it the weather to show: `presenter.getDetail(detailId)`. 

{% highlight kotlin %}
class DetailActivity : AppCompatActivity(), DetailContract.View {

    // The id of the weather to display, passed by intent argument
    private val detailId by argument<String>(ARG_WEATHER_ITEM_ID)

    // our presenter
    override val presenter: DetailContract.Presenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
    }

    override fun onStart() {
        super.onStart()

        // subscribe to presenter
        presenter.subscribe(this)

        // Ask for display
        presenter.getDetail(detailId)
    }

    override fun onStop() {
        // unsubscribe to presenter when view will go away
        presenter.unSubscribe()

        super.onStop()
    }

    // View contract
    override fun showDetail(weather: DailyForecastModel){
        // show the detail ...
    }
}
{% endhighlight %}

#### Writing it with Koin

To bind a Presenter for our Activity (or a Fragment), we just need to declare our Presenter as a `factory`.  A factory is a definition for short live component. It's perfect to match
the Activty/Fragment case, and recreate a Presenter each time teh view is created.

{% highlight kotlin %}
val weatherAppModule = module {
    // Presenter for Detail View
    // inject WeatherRepository by constructor
    factory<DetailContract.Presenter> { DetailPresenter(get()) }

    // Weather Data Repository
    single<WeatherRepository>(createOnStart = true) { WeatherRepositoryImpl()) }
}
{% endhighlight %}

#### More complete MVP app

You can find the complete Weather App application in MVP style in the Koin examples directory: [weather-app-mvp](https://github.com/InsertKoinIO/koin/tree/master/koin-projects/examples/android-mvp)

### How to build MVVM


To illustrate the eaxmples below, we will take a small usecase: a view that has to display weather detail.

A very good explaination article is one from Florina Muntenescu: [Android Architecture Patterns Part 3:
Model-View-ViewModel](https://medium.com/upday-devs/android-architecture-patterns-part-3-model-view-viewmodel-e7eeee76b73b)


#### The ViewModel

In MVVM architecture style, no need of Contract as in MVP. But wie will expose `states` and `events`. Let's write a ViewModel, a component dedicated to present the weather data. we need a dependency, injected by constructor: `val weatherRepository: WeatherRepository`. This will help us request weather data. 

Note here that we expose ViewModel `states`, to publish new data.

The ViewModel will be handle by Android lifecycle. The `getDetail` function get the data, and push `DetailViewModel` states update.

{% highlight kotlin %}
class DetailViewModel(
    val weatherRepository: WeatherRepository
) : RxViewModel() {

    private val _states = MutableLiveData<ViewModelState>()
    val states: LiveData<ViewModelState>
        get() = _states

    fun getDetail(id : String) {
        _states.value = LoadingState
        launch {
            // get detail in Bacground
            val detail = weatherRepository.getWeatherDetail(id)
            if (detail != null){
                _states.value = WeatherDetailState(detail)
            } else {
                _states.value = ErrorState(IllegalStateException("no detail found"))
            }
        }
    }

    data class WeatherDetailState(val weather: DailyForecastModel) : ViewModelState()
}
{% endhighlight %}

#### The View

Let's use the ViewModel in our View. We just need to inject it  with `val viewModel: DetailViewModel by viewModel()`. 

Then we have to listen to our ViewModel (`viewModel.states.observe`), and ask it the weather to show: `viewModel.getDetail(detailId)`. 

{% highlight kotlin %}
class DetailActivity : AppCompatActivity(), DetailContract.View {

    // The id of the weather to display, passed by intent argument
    private val detailId by argument<String>(ARG_WEATHER_ITEM_ID)

    // our ViewModel
    val viewModel: DetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //...
        
        // Observe States
        viewModel.states.observe(this, Observer { state ->
            state?.let {
                when (state) {
                    is ErrorState -> showError(state.error)
                    is DetailViewModel.WeatherDetailState -> showDetail(state.weather)
                }
            }
        })
        // Ask for details
        viewModel.getDetail(detailId)
    }
}
{% endhighlight %}

#### Writing it with Koin

To bind a ViewModel to the Activity (or a Fragment), we just need to declare our ViewModel with `viewModel` keyword.  

{% highlight kotlin %}
val weatherAppModule = module {
    // DetailViewModel
    // inject by constructor
    viewModel { DetailViewModel(get()) }

    // Weather Data Repository
    single<WeatherRepository>(createOnStart = true) { WeatherRepositoryImpl()) }
}
{% endhighlight %}

#### More complete MVVM app

You can find the complete Weather App application in MVVP style in the Koin examples directory: [weather-app-mvvm](https://github.com/InsertKoinIO/koin/tree/master/koin-projects/examples/android-mvvm)

### How to build a Multi Modules App

TBD

### How to build a UserSession

TBD
