package koin.sampleapp.weather

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import koin.sampleapp.json.getLocation
import koin.sampleapp.service.WeatherWS

/**
 * Created by arnaud on 25/09/2017.
 */
class WeatherPresenter(val weatherWS: WeatherWS) : WeatherContract.Presenter {

    override lateinit var view: WeatherContract.View
    private var currentRequest: Disposable? = null
    val DEFAULT_LANG = "EN"

    override fun start() {

    }

    override fun getWeather(location: String) {
        currentRequest = weatherWS.geocode(location)
                .map { it.getLocation() ?: throw IllegalStateException("No Location data") }
                .flatMap { location -> weatherWS.weather(location.lat, location.lng, DEFAULT_LANG) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ weather -> view.displayWeather(weather, location) }, { error -> error("Got error $error") })
    }

}