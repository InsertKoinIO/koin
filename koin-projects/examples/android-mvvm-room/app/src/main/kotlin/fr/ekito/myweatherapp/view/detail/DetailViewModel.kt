package fr.ekito.myweatherapp.view.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.domain.DailyForecastModel
import fr.ekito.myweatherapp.util.mvvm.RxViewModel
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.rx.with
import fr.ekito.myweatherapp.view.ErrorState
import fr.ekito.myweatherapp.view.LoadingState
import fr.ekito.myweatherapp.view.ViewModelState

class DetailViewModel(
    val weatherRepository: WeatherRepository,
    val schedulerProvider: SchedulerProvider
) : RxViewModel() {

    private val _states = MutableLiveData<ViewModelState>()
    val states: LiveData<ViewModelState>
        get() = _states

    fun getDetail(id: String) {
        _states.value = LoadingState
        launch {
            weatherRepository.getWeatherDetail(id).with(schedulerProvider).subscribe(
                { detail ->
                    _states.value = WeatherDetailState(detail)
                }, { error -> _states.value = ErrorState(error) })
        }
    }

    data class WeatherDetailState(val weather: DailyForecastModel) : ViewModelState()
}