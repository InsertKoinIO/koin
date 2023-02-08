package org.koin.sample.android.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.koin.core.Koin
import org.koin.core.time.measureDurationForResult
import org.koin.dsl.koinApplication
import kotlin.math.roundToInt

object PerfRunner {

    suspend fun runAll(scope: CoroutineScope): PerfResult {
        val results = (1..10).map { i -> withContext(scope.coroutineContext) { runScenario(i) } }
        val avgStart = (results.sumOf { it.first } / results.size).round(100)
        val avgExec = (results.sumOf { it.second } / results.size).round(1000)

        println("Avg start time: $avgStart")
        println("Avg execution time: $avgExec")
        return PerfResult(avgStart,avgExec)
    }

    private fun runScenario(index: Int): Pair<Double, Double> {
        val (app, duration) = measureDurationForResult {
            koinApplication {
                modules(perfModule400())
            }
        }
        println("Perf[$index] start in $duration ms")

        val koin = app.koin
        val (_, executionDuration) = measureDurationForResult {
            koinScenario(koin)
        }
        println("Perf[$index] run in $executionDuration ms")
        app.close()
        return Pair(duration, executionDuration)
    }

    private fun koinScenario(koin: Koin){
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
    var isStartOk = startTime <= worstMaxStartTime
    var isExecOk = execTime <= worstExecTime
    var hasFail = false

    fun applyLimits(limits: PerfLimit) {
        worstMaxStartTime = limits.startTime
        worstExecTime = limits.execTime

        isStartOk = startTime < worstMaxStartTime
        isExecOk = execTime < worstExecTime
        hasFail = !isStartOk || !isExecOk
    }
}