package fr.ekito.myweatherapp.util.rx

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Application providers
 */
class ApplicationSchedulerProvider : SchedulerProvider {
    override fun io() = Schedulers.io()

    override fun ui() = AndroidSchedulers.mainThread()

    override fun computation() = Schedulers.computation()
}