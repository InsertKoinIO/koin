package fr.ekito.myweatherapp.util.mvvm

import android.arch.lifecycle.ViewModel
import android.support.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * ViewModel for Rx Jobs
 *
 * launch() - launch a Rx request
 * clear all request on stop
 */
abstract class RxViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

    fun launch(job: () -> Disposable) {
        disposables.add(job())
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}