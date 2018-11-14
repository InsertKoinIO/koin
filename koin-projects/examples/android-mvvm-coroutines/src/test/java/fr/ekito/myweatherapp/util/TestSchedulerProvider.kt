package fr.ekito.myweatherapp.util

import fr.ekito.myweatherapp.util.coroutines.SchedulerProvider
import kotlinx.coroutines.Dispatchers.Unconfined

class TestSchedulerProvider : SchedulerProvider {
    override fun ui() = Unconfined
}