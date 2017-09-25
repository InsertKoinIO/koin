package fr.ekito.myweatherapp

import io.reactivex.schedulers.Schedulers
import koin.sampleapp.rx.SchedulerProvider

class TestSchedulerProvider : SchedulerProvider {
    override fun io() = Schedulers.trampoline()

    override fun ui() = Schedulers.trampoline()

    override fun computation() = Schedulers.trampoline()
}