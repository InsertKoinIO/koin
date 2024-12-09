package org.koin.benchmark

import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.runner.Defaults.WARMUP_ITERATIONS
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
// jvmBenchmark can access declarations from jvmMain!
@Warmup(iterations = WARMUP_ITERATIONS, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
class StartupBenchmark {
    private var module : Module? = null

    @Setup
    fun setUp() {
        module = perfModule400()
    }

    @Benchmark
    fun startupKoinWith400Fun() {
        koinApplication {
            modules(perfModule400())
        }
    }

    @Benchmark
    fun startupKoinWith400() {
        koinApplication {
            modules(module!!)
        }
    }
}