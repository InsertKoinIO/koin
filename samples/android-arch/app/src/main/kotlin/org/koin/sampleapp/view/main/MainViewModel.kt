package org.koin.sampleapp.view.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import org.koin.sampleapp.repository.WeatherRepository
import org.koin.sampleapp.util.rx.SchedulerProvider
import org.koin.sampleapp.util.rx.with

class MainViewModel(private val weatherRepository: WeatherRepository, private val scheduler: SchedulerProvider) : ViewModel() {

    private var disposable: Disposable? = null
    val searchOk = MutableLiveData<MainUIModel>()

    fun searchWeather(address: String) {
        searchOk.value = MainUIModel(address, true)
        disposable = weatherRepository.getWeather(address).toCompletable()
                .with(scheduler)
                .subscribe(
                        {
                            searchOk.value = MainUIModel(address, false, true)
                            searchOk.value = MainUIModel(address)
                        },
                        { err ->
                            searchOk.value = MainUIModel(address, error = err)
                            System.err.println("MainViewModel : $err")
                        })
    }

    override fun onCleared() {
        disposable?.dispose()
        disposable = null
        super.onCleared()
    }
}

data class MainUIModel(val searchText: String = "", val isLoading: Boolean = false, val success: Boolean = false, val error: Throwable? = null)