package fr.ekito.myweatherapp.view.detail

import fr.ekito.myweatherapp.data.repository.WeatherRepository
import fr.ekito.myweatherapp.util.mvp.RxPresenter
import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import fr.ekito.myweatherapp.util.rx.with

class DetailPresenter(
    private val weatherRepository: WeatherRepository,
    private val schedulerProvider: SchedulerProvider
) : RxPresenter<DetailContract.View>(), DetailContract.Presenter {

    override var view: DetailContract.View? = null

    override fun getDetail(id: String) {
        launch {
            weatherRepository.getWeatherDetail(id).with(schedulerProvider).subscribe(
                { detail ->
                    view?.showDetail(detail)
                }, { error -> view?.showError(error) })
        }
    }
}