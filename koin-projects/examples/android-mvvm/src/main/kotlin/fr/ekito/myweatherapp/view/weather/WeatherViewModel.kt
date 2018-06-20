package fr.ekito.myweatherapp.view.weather

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.domain.DailyForecastModel
import fr.ekito.myweatherapp.util.mvvm.RxViewModel
import fr.ekito.myweatherapp.util.mvvm.SingleLiveEvent
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.rx.with
import fr.ekito.myweatherapp.view.ErrorState
import fr.ekito.myweatherapp.view.LoadingState
import fr.ekito.myweatherapp.view.ViewModelEvent
import fr.ekito.myweatherapp.view.ViewModelState

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val schedulerProvider: SchedulerProvider
) : RxViewModel() {

    private val _states = MutableLiveData<ViewModelState>()
    val states: LiveData<ViewModelState>
        get() = _states

    private val _events = SingleLiveEvent<ViewModelEvent>()
    val events: LiveData<ViewModelEvent>
        get() = _events

    /**
     * Load new weather for given location
     * notify for loading: LoadingLocationEvent / LoadLocationFailedEvent
     * push WeatherListState if it succeed
     */
    fun loadNewLocation(newLocation: String) {
        _events.value = LoadingLocationEvent(newLocation)
        launch {
            weatherRepository.getWeather(newLocation)
                .with(schedulerProvider)
                .subscribe(
                    { weather -> _states.value = WeatherListState.from(weather) },
                    { error -> _events.value = LoadLocationFailedEvent(newLocation, error) })
        }
    }

    /**
     * Retrieve previously loaded weather and push view states
     */
    fun getWeather() {
        _states.value = LoadingState
        launch {
            weatherRepository.getWeather()
                .with(schedulerProvider)
                .subscribe(
                    { weather -> _states.value = WeatherListState.from(weather) },
                    { error -> _states.value = ErrorState(error) })
        }
    }

    data class WeatherListState(
        val location: String,
        val first: DailyForecastModel,
        val lasts: List<DailyForecastModel>
    ) : ViewModelState() {
        companion object {
            fun from(list: List<DailyForecastModel>): WeatherListState {
                return if (list.isEmpty()) error("weather list should not be empty")
                else {
                    val first = list.first()
                    val location = first.location
                    WeatherListState(location, first, list.takeLast(list.size - 1))
                }
            }
        }
    }

    data class LoadingLocationEvent(val location: String) : ViewModelEvent()
    data class LoadLocationFailedEvent(val location: String, val error: Throwable) :
        ViewModelEvent()
}