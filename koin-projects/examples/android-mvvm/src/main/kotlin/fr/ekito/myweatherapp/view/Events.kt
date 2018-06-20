package fr.ekito.myweatherapp.view

/**
 * Abstract Event from ViewModel
 */
open class ViewModelEvent

/**
 * Generic Loading Event
 */
object LoadingEvent : ViewModelEvent()

/**
 * Generic Success Event
 */
object SuccessEvent : ViewModelEvent()

/**
 * Generic Failed Event
 */
data class FailedEvent(val error: Throwable) : ViewModelEvent()