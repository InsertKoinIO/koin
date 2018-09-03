package fr.ekito.myweatherapp.view.splash

import android.arch.lifecycle.LiveData
import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.util.mvvm.CoroutineViewModel
import fr.ekito.myweatherapp.util.mvvm.SingleLiveEvent
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.view.Event
import fr.ekito.myweatherapp.view.FailedEvent
import fr.ekito.myweatherapp.view.LoadingEvent
import fr.ekito.myweatherapp.view.SuccessEvent

class SplashViewModel(
    private val weatherRepository: WeatherRepository,
    schedulerProvider: SchedulerProvider
) : CoroutineViewModel(schedulerProvider) {

    /*
     * We use SingleLiveEvent to publish "events"
     * No need to publish and retain any view state
     */
    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events

    fun getLastWeather() {
        _events.value = LoadingEvent
        launch {
            try {
                weatherRepository.getWeather().await()
                _events.value = SuccessEvent
            } catch (error: Throwable) {
                _events.value = FailedEvent(error)
            }
        }
    }
}