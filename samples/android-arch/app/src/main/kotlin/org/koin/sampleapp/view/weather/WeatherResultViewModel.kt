package org.koin.sampleapp.view.weather

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.repository.json.getDailyForecasts
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with
import org.koin.standalone.KoinComponent

/**
 * Weather Presenter
 */
class WeatherResultViewModel(val weatherRepository: WeatherRepository, val scheduler: SchedulerProvider) : ViewModel(), KoinComponent {

    val searchList = MutableLiveData<List<DailyForecastModel>>()
    val selectDetail = MutableLiveData<Boolean>()

    val disposables = CompositeDisposable()


    fun getWeatherList(address: String) {
        disposables.add(weatherRepository.getWeather(address).map { it.getDailyForecasts() }.with(scheduler)
                .subscribe { list -> searchList.value = list })
    }

    fun selectDetail(detail: DailyForecastModel) {
        disposables.add(weatherRepository.selectDetail(detail).with(scheduler).subscribe { selectDetail.value = true })
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}