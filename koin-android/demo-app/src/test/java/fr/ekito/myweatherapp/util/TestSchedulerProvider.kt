package fr.ekito.myweatherapp.util

import io.reactivex.schedulers.Schedulers
import koin.sampleapp.util.rx.SchedulerProvider

class TestSchedulerProvider : SchedulerProvider {
    override fun io() = Schedulers.trampoline()

    override fun ui() = Schedulers.trampoline()

    override fun computation() = Schedulers.trampoline()
}