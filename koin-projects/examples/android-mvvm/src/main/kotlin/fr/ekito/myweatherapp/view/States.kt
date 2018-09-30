package fr.ekito.myweatherapp.view

/**
 * Abstract State pushed by ViewModel
 */
open class ViewModelState

/**
 * Generic Loading State
 */
object Loading : ViewModelState()

/**
 * Generic Error state
 * @param error - caught error
 */
data class Failed(val error: Throwable) : ViewModelState()