package org.koin.benchmark

import kotlinx.coroutines.CoroutineScope
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import kotlin.math.roundToInt
import kotlin.time.measureTimedValue

object PerfRunner {

    fun runAll(): PerfResult {
        val results = (1..10).map { i -> runScenario(i) }
        val avgStart = (results.sumOf { it.first } / results.size).round(100)
        val avgExec = (results.sumOf { it.second } / results.size).round(10000)

        println("Avg start: $avgStart ms")
        println("Avg execution: $avgExec ms")
        return PerfResult(avgStart,avgExec)
    }

    fun runScenario(index: Int): Pair<Double, Double> {
        val appDuration = measureTimedValue {
            koinApplication {
                modules(
                    perfModule400()
                )
            }
        }
        val appDurationValue = appDuration.duration.inWholeMicroseconds / 1000.0

        println("Perf[$index] start in $appDurationValue ms")

        val koin: Koin = appDuration.value.koin

        val scenarioDuration = measureTimedValue {
            koinScenario(koin)
        }
        val scenarioDurationValue = scenarioDuration.duration.inWholeMicroseconds / 1000.0
        println("Perf[$index] run in $scenarioDurationValue ms")

        return Pair(appDurationValue, scenarioDurationValue)
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