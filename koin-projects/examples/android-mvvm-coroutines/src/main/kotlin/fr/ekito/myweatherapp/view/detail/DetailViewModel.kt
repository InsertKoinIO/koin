package fr.ekito.myweatherapp.view.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.domain.DailyForecastModel
import fr.ekito.myweatherapp.util.mvvm.CoroutineViewModel
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.view.ErrorState
import fr.ekito.myweatherapp.view.LoadingState
import fr.ekito.myweatherapp.view.State

class DetailViewModel(
    private val weatherRepository: WeatherRepository,
    schedulerProvider: SchedulerProvider
) : CoroutineViewModel(schedulerProvider) {

    private val _states = MutableLiveData<State>()
    val states: LiveData<State>
        get() = _states

    fun getDetail(id: String) {
        _states.value = LoadingState
        launch {
            try {
                val detail = weatherRepository.getWeatherDetail(id).await()
                _states.value = WeatherDetailState(detail)
            } catch (error: Throwable) {
                _states.value = ErrorState(error)
            }
        }
    }

    data class WeatherDetailState(val weather: DailyForecastModel) : State()
}