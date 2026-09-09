package org.koin.benchmark

import org.koin.core.Koin
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.runner.Defaults.WARMUP_ITERATIONS
import java.util.concurrent.TimeUnit

/**
 * Three-timer split for KTZ-4829 (Koin vs Metro startup gap).
 *
 * Reproduces the shape the Metro harness measures — register modules, then force realization of
 * every multibinding contributor through getAll — so the cost can be attributed instead of read
 * as one number:
 *
 *  - t0                    module DSL construction alone
 *  - t1 - t0               registration into the registry (indexKey + saveMapping)
 *  - t2 - t1               realization: ~count resolutions plus the getAll scan
 *  - t3                    the getAll scan alone, on an already-realized container
 *  - t4 / t4b              per-resolution overhead, warm
 *
 * Run with `-prof gc`: allocations per op is the metric being driven down, not just ms.
 * t3 across counts answers whether getAll is O(k) or O(total definitions).
 */
@State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = WARMUP_ITERATIONS, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
class GetAllBenchmark {

    @Param("100", "500", "1800")
    var count: Int = 0

    private lateinit var warm: Koin

    @Setup(Level.Trial)
    fun setUp() {
        warm = koinApplication { modules(multibindingModule(count)) }.koin
        // Realize everything, so t3 measures the scan and not the creation.
        warm.getAll<ScaleMarker>()
        warm.get<ScaleRoot>()
    }

    @TearDown(Level.Trial)
    fun tearDown() {
        warm.close()
    }

    // Modules are rebuilt per invocation on purpose: InstanceFactory instances are owned by the
    // module, so reusing one across containers would hand t2 singletons that are already created.
    @Benchmark
    fun t0_module_build_only(): Any = multibindingModule(count)

    @Benchmark
    fun t1_registration_only(): Koin = koinApplication { modules(multibindingModule(count)) }.koin

    @Benchmark
    fun t2_registration_plus_realize(): Int {
        val koin = koinApplication { modules(multibindingModule(count)) }.koin
        return koin.getAll<ScaleMarker>().size
    }

    @Benchmark
    fun t3_getAll_pure_scan(): Int = warm.getAll<ScaleMarker>().size

    @Benchmark
    fun t4_get_warm_unqualified(): Any = warm.get<ScaleRoot>()

    @Benchmark
    fun t4b_get_warm_qualified(): Any = warm.get<ScaleContributor>(named("c0"))
}
