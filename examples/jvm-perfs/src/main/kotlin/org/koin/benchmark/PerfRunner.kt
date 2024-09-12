package org.koin.benchmark

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.lazyModules
import org.koin.core.logger.Level
import org.koin.core.module.LazyModule
import org.koin.core.module.Module
import org.koin.core.time.inMs
import org.koin.core.waitAllStartJobs
import org.koin.dsl.koinApplication
import kotlin.math.roundToInt
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

object PerfRunner {

    fun runAll(useDebugLogs : Boolean = false, module : () -> Module): PerfResult {
        val results = (1..10).map { i -> runScenario(i,useDebugLogs, module) }
        val avgStart = (results.sumOf { it.first } / results.size).round(100)
        val avgExec = (results.sumOf { it.second } / results.size).round(10000)

        println("Avg start: $avgStart ms")
        println("Avg execution: $avgExec ms")
        return PerfResult(avgStart,avgExec)
    }

    fun runAllLazy(useDebugLogs : Boolean = false, module : () -> LazyModule): PerfResult {
        val results = (1..10).map { i -> runScenarioLazy(i,useDebugLogs, module) }
        val avgStart = (results.sumOf { it.first } / results.size).round(100)
        val avgExec = (results.sumOf { it.second } / results.size).round(10000)

        println("Avg start: $avgStart ms")
        println("Avg execution: $avgExec ms")
        return PerfResult(avgStart,avgExec)
    }

    fun runScenario(index: Int, useDebugLogs: Boolean, module: () -> Module): Pair<Double, Double> {
        val appDuration = measureTimedValue {
            koinApplication {
                if (useDebugLogs){
                    printLogger(level = Level.DEBUG)
                }
                modules(
                    module()
                )
            }
        }
        return onScenarioRun(appDuration, index)
    }

    fun runScenarioLazy(index: Int, useDebugLogs: Boolean, lazyModule: () -> LazyModule): Pair<Double, Double> {
        val appDuration = measureTimedValue {
            koinApplication {
                if (useDebugLogs){
                    printLogger(level = Level.DEBUG)
                }
                lazyModules(
                    lazyModule()
                )
            }
        }
        appDuration.value.koin.waitAllStartJobs()
        return onScenarioRun(appDuration, index)
    }

    private fun onScenarioRun(
        appDuration: TimedValue<KoinApplication>,
        index: Int
    ): Pair<Double, Double> {
        val koin: Koin = appDuration.value.koin
        val scenarioDuration = measureTimedValue {
            koinScenario(koin)
        }

        val appDurationValue = appDuration.duration.inMs
        val scenarioDurationValue = scenarioDuration.duration.inMs
        println("Perf[$index] start in $appDurationValue ms")
        println("Perf[$index] run in $scenarioDurationValue ms")

        return Pair(appDurationValue, scenarioDurationValue)
    }

    fun koinScenario(koin: Koin){
        koin.get<A31>()
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