package org.koin.benchmark

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

object PerfRunner {

    suspend fun runAll(scope: CoroutineScope): PerfResult {
        val results = (1..10).map { i -> withContext(scope.coroutineContext) { runScenario(i) } }
        val avgStartInMs = (results.sumOf { it.first.inWholeMilliseconds }.toDouble() / results.size).round(100)
        val avgExecInMs = (results.sumOf { it.second.inWholeMilliseconds }.toDouble() / results.size).round(1000)

        println("Avg start: $avgStartInMs ms")
        println("Avg execution: $avgExecInMs ms")
        return PerfResult(avgStartInMs, avgExecInMs)
    }

//    @OptIn(KoinExperimentalAPI::class)
    fun runScenario(index: Int): Pair<Duration, Duration> {
        val (app, duration) = measureTimedValue {
            koinApplication {
                modules(
                    perfModule400()
                )
            }
        }
        println("Perf[$index] start in ${duration.inWholeMilliseconds} ms")

        val koin: Koin = app.koin

//        runBlocking {
//            koin.awaitAllStartJobs()
//        }

        val executionDuration = measureTime {
            koinScenario(koin)
        }
        println("Perf[$index] run in ${executionDuration.inWholeMilliseconds} ms")
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

fun Double.round(digits : Int) : Double = (this * digits).roundToInt() / digits.toDouble()

data class PerfLimit(val startTime : Double, val execTime : Double)

data class PerfResult(val startTime : Double, val execTime : Double) {
    var worstMaxStartTime = 0.0
    var worstExecTime = 0.0
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