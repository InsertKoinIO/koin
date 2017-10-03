package koin.sampleapp.util.rx

import io.reactivex.Scheduler


interface SchedulerProvider {
    fun io(): Scheduler
    fun ui(): Scheduler
    fun computation(): Scheduler
}