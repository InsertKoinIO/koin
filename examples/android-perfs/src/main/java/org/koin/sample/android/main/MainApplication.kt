package org.koin.sample.android.main

import android.app.Application
import kotlinx.coroutines.runBlocking
import org.koin.benchmark.PerfLimit
import org.koin.benchmark.PerfResult
import org.koin.benchmark.PerfRunner.runAll
import org.koin.benchmark.perfModule400

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        runBlocking {
            results = runAll(useDebugLogs = false, module = ::perfModule400)
//            results = runAllLazy(useDebugLogs = false, module = ::perfModule400_LazyFu)
            results!!.applyLimits(limits)
        }
    }

    companion object {
        val limits = PerfLimit(27.0, 0.150)
        var results : PerfResult? = null
    }
}