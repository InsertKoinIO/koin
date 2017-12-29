package org.koin.sampleapp.view.detail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import org.koin.sampleapp.model.DailyForecastModel
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with

/**
 * Weather Presenter
 */
class WeatherDetailViewModel(private val weatherRepository: WeatherRepository, private val scheduler: SchedulerProvider) : ViewModel() {

    private val disposables = CompositeDisposable()
    val detail = MutableLiveData<DailyForecastModel>()

    init {
        getDetail()
    }

    fun getDetail() {
        disposables.add(weatherRepository.getSelectDetail().with(scheduler).subscribe { d ->
            detail.value = d
        })
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}