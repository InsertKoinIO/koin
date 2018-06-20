package fr.ekito.myweatherapp.util.mvp

import android.support.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Base Presenter feature - for Rx Jobs
 *
 * launch() - launch a Rx request
 * clear all request on stop
 */
abstract class RxPresenter<V> : BasePresenter<V> {

    private val disposables = CompositeDisposable()

    fun launch(job: () -> Disposable) {
        disposables.add(job())
    }

    override fun subscribe(view: V) {
        this.view = view
    }

    @CallSuper
    override fun unSubscribe() {
        disposables.clear()
        view = null
    }
}