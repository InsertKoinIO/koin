package fr.ekito.myweatherapp.util.mvp

/**
 * View
 */
interface BaseView<out T : BasePresenter<*>> {

    fun showError(error: Throwable)

    val presenter: T
}