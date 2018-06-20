package fr.ekito.myweatherapp.view

/**
 * Abstract ViewModel State
 */
open class ViewModelState

/**
 * Generic Loading ViewModel State
 */
object LoadingState : ViewModelState()

/**
 * Generic Error ViewModel State
 * @param error - caught error
 */
data class ErrorState(val error: Throwable) : ViewModelState()
