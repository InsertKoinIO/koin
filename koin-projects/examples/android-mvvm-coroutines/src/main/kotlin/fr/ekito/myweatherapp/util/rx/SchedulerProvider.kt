package fr.ekito.myweatherapp.util.rx

import kotlinx.coroutines.experimental.CoroutineDispatcher

/**
 * Rx Scheduler Provider
 */
interface SchedulerProvider {
    fun ui(): CoroutineDispatcher
}