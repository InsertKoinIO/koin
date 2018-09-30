package fr.ekito.myweatherapp.util.mvvm

import android.arch.lifecycle.ViewModel
import fr.ekito.myweatherapp.util.coroutines.SchedulerProvider
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job

/**
 * ViewModel for Coroutines Jobs
 *
 * launch() - launch a Rx request
 * clear all request on stop
 */
abstract class CoroutineViewModel(private val schedulerProvider: SchedulerProvider) : ViewModel() {
    var jobs = listOf<Job>()

    fun launch(code: suspend CoroutineScope.() -> Unit) {
        jobs += kotlinx.coroutines.experimental.launch(schedulerProvider.ui(), block = code)
    }

    override fun onCleared() {
        super.onCleared()
        jobs.forEach { it.cancel() }
    }
}