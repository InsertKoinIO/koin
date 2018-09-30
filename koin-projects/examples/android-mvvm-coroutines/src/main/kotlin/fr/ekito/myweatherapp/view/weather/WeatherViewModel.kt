package fr.ekito.myweatherapp.view.weather

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import fr.ekito.myweatherapp.domain.repository.WeatherRepository
import fr.ekito.myweatherapp.domain.entity.DailyForecast
import fr.ekito.myweatherapp.util.mvvm.CoroutineViewModel
import fr.ekito.myweatherapp.util.mvvm.SingleLiveEvent
import fr.ekito.myweatherapp.util.coroutines.SchedulerProvider
import fr.ekito.myweatherapp.view.ErrorState
import fr.ekito.myweatherapp.view.Event
import fr.ekito.myweatherapp.view.LoadingState
import fr.ekito.myweatherapp.view.State

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    schedulerProvider: SchedulerProvider
) : CoroutineViewModel(schedulerProvider) {

    private val _states = MutableLiveData<State>()
    val states: LiveData<State>
        get() = _states

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events

    /**
     * Load new weather for given location
     * notify for loading: LoadingLocationEvent / LoadLocationFailedEvent
     * push WeatherListState if it succeed
     */
    fun loadNewLocation(newLocation: String) {
        launch {
            _events.value = LoadingLocationEvent(newLocation)
            try {
                val weather = weatherRepository.getWeather(newLocation).await()
                _states.value = WeatherListState.from(weather)
            } catch (error: Throwable) {
                _events.value = LoadLocationFailedEvent(newLocation, error)
            }
        }
    }

    /**
     * Retrieve previously loaded weather and push view states
     */
    fun getWeather() {
        launch {
            _states.value = LoadingState
            try {
                val weather = weatherRepository.getWeather().await()
                _states.value = WeatherListState.from(weather)
            } catch (error: Throwable) {
                _states.value = ErrorState(error)
            }
        }
    }

    data class WeatherListState(
        val location: String,
        val first: DailyForecast,
        val lasts: List<DailyForecast>
    ) : State() {
        companion object {
            fun from(list: List<DailyForecast>): WeatherListState {
                return if (list.isEmpty()) error("weather list should not be empty")
                else {
                    val first = list.first()
                    val location = first.location
                    WeatherListState(location, first, list.takeLast(list.size - 1))
                }
            }
        }
    }

    data class LoadingLocationEvent(val location: String) : Event()
    data class LoadLocationFailedEvent(val location: String, val error: Throwable) : Event()
}