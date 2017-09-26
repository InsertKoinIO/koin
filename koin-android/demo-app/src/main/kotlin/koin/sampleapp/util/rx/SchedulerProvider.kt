package koin.sampleapp.util.rx

import io.reactivex.Scheduler

/**
 * Created by arnaud on 25/09/2017.
 */
interface SchedulerProvider {
    fun io(): Scheduler
    fun ui(): Scheduler
    fun computation(): Scheduler
}