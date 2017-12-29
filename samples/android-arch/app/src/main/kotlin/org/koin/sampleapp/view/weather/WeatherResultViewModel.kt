package org.koin.sampleapp.view.weather

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.repository.json.getDailyForecasts
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with

/**
 * Weather Presenter
 */
class WeatherResultViewModel(private val weatherRepository: WeatherRepository, private val scheduler: SchedulerProvider, address: String) : ViewModel() {

    val searchList = MutableLiveData<WeatherResultUIModel>()

    val disposables = CompositeDisposable()

    init {
        getWeatherList(address)
    }

    lateinit var cacheList: List<DailyForecastModel>

    fun getWeatherList(address: String) {
        disposables.add(weatherRepository.getWeather(address).map { it.getDailyForecasts() }.with(scheduler)
                .subscribe
                { list ->
                    searchList.value = WeatherResultUIModel(list)
                    cacheList = list
                }
        )
    }

    fun selectDetail(detail: DailyForecastModel) {
        disposables.add(weatherRepository.selectDetail(detail).with(scheduler).subscribe {
            searchList.value = WeatherResultUIModel(cacheList, true)
            searchList.value = WeatherResultUIModel(cacheList)
        })
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}


data class WeatherResultUIModel(val list: List<DailyForecastModel>, val selected: Boolean = false)