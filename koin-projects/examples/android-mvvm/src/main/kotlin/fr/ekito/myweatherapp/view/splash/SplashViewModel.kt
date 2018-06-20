package fr.ekito.myweatherapp.view.splash

import android.arch.lifecycle.LiveData
import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.util.mvvm.RxViewModel
import fr.ekito.myweatherapp.util.mvvm.SingleLiveEvent
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.rx.with
import fr.ekito.myweatherapp.view.FailedEvent
import fr.ekito.myweatherapp.view.LoadingEvent
import fr.ekito.myweatherapp.view.SuccessEvent
import fr.ekito.myweatherapp.view.ViewModelEvent

class SplashViewModel(
    private val weatherRepository: WeatherRepository,
    private val schedulerProvider: SchedulerProvider
) : RxViewModel() {

    /*
     * We use SingleLiveEvent to publish "events"
     * No need to publish and retain any view state
     */
    private val _events = SingleLiveEvent<ViewModelEvent>()
    val events: LiveData<ViewModelEvent>
        get() = _events

    fun getLastWeather() {
        _events.value = LoadingEvent
        launch {
            weatherRepository.getWeather().with(schedulerProvider)
                .toCompletable()
                .subscribe({ _events.value = SuccessEvent },
                    { error -> _events.value = FailedEvent(error) })
        }
    }
}