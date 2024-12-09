package org.koin.benchmark

import kotlinx.benchmark.*
import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
//// jvmBenchmark can access declarations from jvmMain!
@Warmup(iterations = 5, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = BenchmarkTimeUnit.SECONDS)
class NativeBenchmark {
    private var module : Module? = null
    private var koin : Koin? = null

    @Setup
    fun setUp() {
        module = perfModule400()
        koin = koinApplication {
            modules(module!!)
        }.koin
    }

    @Benchmark
    fun retrieveDependencyA() {
        koin!!.get<Perfs.A42>()
    }

    @Benchmark
    fun retrieveDependencyB() {
        koin!!.get<Perfs.B42>()
    }
}