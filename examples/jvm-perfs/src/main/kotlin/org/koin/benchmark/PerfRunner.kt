package org.koin.benchmark

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

object PerfRunner {

    suspend fun runAll(scope: CoroutineScope): PerfResult {
        val results = (1..10).map { i -> withContext(scope.coroutineContext) { runScenario(i) } }
        val avgStart = results.map { it.first.inWholeMilliseconds }.average().milliseconds
        val avgExec = results.map { it.second.inWholeMilliseconds }.average().milliseconds

        println("Avg start: $avgStart")
        println("Avg execution: $avgExec")
        return PerfResult(avgStart, avgExec)
    }

    fun runScenario(index: Int): Pair<Duration, Duration> {
        val (app, duration) = measureTimedValue {
            koinApplication {
                modules(
                    perfModule400()
                )
            }
        }
        println("Perf[$index] start in $duration")

        val koin: Koin = app.koin

//        runBlocking {
//            koin.awaitAllStartJobs()
//        }

        val executionDuration = measureTime {
            koinScenario(koin)
        }
        println("Perf[$index] run in $executionDuration")
        app.close()
        return Pair(duration, executionDuration)
    }

    fun koinScenario(koin: Koin){
        koin.get<A27>()
        koin.get<A31>()
        koin.get<A12>()
        koin.get<A42>()
    }
}

data class PerfLimit(val startTime: Duration, val execTime: Duration)

data class PerfResult(val startTime: Duration, val execTime: Duration) {
    var worstMaxStartTime = Duration.ZERO
    var worstExecTime = Duration.ZERO
    var isStartOk = false
    var isExecOk = false
    var isOk = true

    fun applyLimits(limits: PerfLimit) {
        worstMaxStartTime = limits.startTime
        worstExecTime = limits.execTime

        isStartOk = startTime <= worstMaxStartTime
        isExecOk = execTime <= worstExecTime
        isOk = isStartOk && isExecOk
    }
}