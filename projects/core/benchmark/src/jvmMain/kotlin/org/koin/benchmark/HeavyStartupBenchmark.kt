package org.koin.benchmark

import kotlinx.coroutines.runBlocking
import org.koin.core.lazyModules
import org.koin.core.waitAllStartJobs
import org.koin.dsl.koinApplication
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.runner.Defaults.WARMUP_ITERATIONS
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
// jvmBenchmark can access declarations from jvmMain!
@Warmup(iterations = WARMUP_ITERATIONS, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
class HeavyStartupBenchmark {

    fun modulesList(max : Int) = (1..max).map { perfModule400() }
    fun lazyModulesList(max : Int) = (1..max).map { perfLazyModule400() }

    @Benchmark
    fun start_get_module1() {
        val k = koinApplication {
            modules(perfModule400())
        }.koin

        k.get<Perfs.A25>()
    }

    @Benchmark
    fun start_get_lazy_module1() = runBlocking {
        val k = koinApplication {
            lazyModules(perfLazyModule400())
        }.koin

        k.waitAllStartJobs()

        k.get<Perfs.A25>()
    }

    @Benchmark
    fun start_get_module100() {
        val k = koinApplication {
            modules(modulesList(100))
        }.koin
        k.get<Perfs.B50>()
    }

    @Benchmark
    fun start_get_lazy_module100() = runBlocking {
        val k = koinApplication {
            lazyModules(lazyModulesList(100))
        }.koin

        k.waitAllStartJobs()

        k.get<Perfs.B50>()
    }

    @Benchmark
    fun start_get_module1000() {
        val k = koinApplication {
            modules(modulesList(1000))
        }.koin

        k.get<Perfs.C75>()
    }


    @Benchmark
    fun start_get_lazy_module1000() = runBlocking {
        val k = koinApplication {
            lazyModules(lazyModulesList(1000))
        }.koin

        k.waitAllStartJobs()

        k.get<Perfs.C75>()
    }
}