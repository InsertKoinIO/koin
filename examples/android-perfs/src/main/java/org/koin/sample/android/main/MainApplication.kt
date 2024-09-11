package org.koin.sample.android.main

import android.app.Application
import kotlinx.coroutines.runBlocking
import org.koin.benchmark.PerfLimit
import org.koin.benchmark.PerfResult
import org.koin.benchmark.PerfRunner.runAll

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        runBlocking {
            results = runAll()
            results!!.applyLimits(limits)
        }
    }

    companion object {
        val limits = PerfLimit(11.0, 0.150)
        var results : PerfResult? = null
    }
}