package org.koin.benchmark

import org.koin.benchmark.PerfRunner.koinScenario
import org.koin.dsl.koinApplication
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2, warmups = 0)
@Measurement(iterations = 5, time = 2)
@Warmup(iterations = 1)
open class BenchmarkClass {
    private val nestedModules = buildNestedModule(
        depth = 128,
        width = 256,
    )

    @Benchmark
    fun emptyStart() {
        koinApplication {
        }
    }

    @Benchmark
    fun start400() {
        koinApplication {
            modules(perfModule400())
        }.koin
    }

    @Benchmark
    fun start400AndInject() {
        val koin = koinApplication {
            modules(perfModule400())
        }.koin
        koinScenario(koin)
    }

    @Benchmark
    fun flattenRecursive() {
        org.koin.core.module.flatten(nestedModules)
    }

    @Benchmark
    fun flattenIterative() {
        flattenIterative(nestedModules)
    }
}