package fr.ekito.myweatherapp.view

/**
 * Abstract Event from ViewModel
 */
open class Event

/**
 * Generic Loading Event
 */
object LoadingEvent : Event()

/**
 * Generic Success Event
 */
object SuccessEvent : Event()

/**
 * Generic Failed Event
 */
data class FailedEvent(val error: Throwable) : Event()