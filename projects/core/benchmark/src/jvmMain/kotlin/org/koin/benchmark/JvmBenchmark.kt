package org.koin.benchmark

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Defaults.WARMUP_ITERATIONS
import java.util.concurrent.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
// jvmBenchmark can access declarations from jvmMain!
@Warmup(iterations = WARMUP_ITERATIONS, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
class JvmBenchmark {

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
    fun retrieveDependency() {
        koin!!.get<Perfs.A42>()
    }

    // Tight loop variant: amortizes JMH per-op overhead so the per-resolve cost (and any
    // allocation churn from indexKey() rebuild) is easier to see at nanosecond resolution.
    // Use with `-prof gc` to read alloc/op against the unrolled count.
    @Benchmark
    @OperationsPerInvocation(1000)
    fun retrieveDependency_tight() {
        val k = koin!!
        repeat(1000) {
            k.get<Perfs.A42>()
        }
    }
}