package fr.ekito.myweatherapp.util.mvp

/**
 * Presenter
 */
interface BasePresenter<V> {

    fun subscribe(view: V)

    fun unSubscribe()

    var view : V?

}