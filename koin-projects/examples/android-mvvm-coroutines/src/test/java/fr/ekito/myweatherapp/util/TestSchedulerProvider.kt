package fr.ekito.myweatherapp.util

import fr.ekito.myweatherapp.util.rx.SchedulerProvider
import kotlinx.coroutines.experimental.Unconfined

class TestSchedulerProvider : SchedulerProvider {
    override fun ui() = Unconfined
}