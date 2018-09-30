package fr.ekito.myweatherapp.util.coroutines

import kotlinx.coroutines.experimental.android.UI

/**
 * Application providers
 */
class ApplicationSchedulerProvider : SchedulerProvider {
    override fun ui() = UI
}