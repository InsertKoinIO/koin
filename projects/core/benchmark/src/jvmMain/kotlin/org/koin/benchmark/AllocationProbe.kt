package org.koin.benchmark

import com.sun.management.ThreadMXBean
import org.koin.core.Koin
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import java.lang.management.ManagementFactory

/**
 * Bytes-per-op probe for KTZ-4829.
 *
 * kotlinx-benchmark 0.4.16 accepts only nativeFork / nativeGCAfterIteration / jvmForks /
 * jsUseBridge as advanced options, so JMH's `-prof gc` cannot be reached through the Gradle
 * plugin. getThreadAllocatedBytes gives the same number more directly: exact bytes allocated
 * by this thread, with no sampling.
 *
 * The exit criterion in the ticket is stated in allocations, not milliseconds — a warm
 * resolution should approach 0 B/op once the eager debug strings are gone.
 *
 * Run with: ./gradlew :core:benchmark:allocProbe
 */
private val threadBean = ManagementFactory.getThreadMXBean() as ThreadMXBean

private fun allocatedBytes(): Long = threadBean.currentThreadAllocatedBytes

private class Result(val name: String, val bytesPerOp: Double, val nanosPerOp: Double)

private fun measure(name: String, warmup: Int, reps: Int, block: () -> Any?): Result {
    repeat(warmup) { consume(block()) }

    val bytesBefore = allocatedBytes()
    val startNanos = System.nanoTime()
    repeat(reps) { consume(block()) }
    val elapsedNanos = System.nanoTime() - startNanos
    val bytesUsed = allocatedBytes() - bytesBefore

    // The probe's own loop allocates nothing, so no baseline subtraction is needed beyond
    // the bean read itself, which happens outside the measured region.
    return Result(name, bytesUsed.toDouble() / reps, elapsedNanos.toDouble() / reps)
}

private var sink: Any? = null

private fun consume(value: Any?) {
    sink = value
}

private fun report(results: List<Result>) {
    val width = results.maxOf { it.name.length }
    println()
    println("%-${width}s %14s %14s".format("Benchmark", "B/op", "ns/op"))
    results.forEach {
        println("%-${width}s %14.1f %14.1f".format(it.name, it.bytesPerOp, it.nanosPerOp))
    }
    println()
}

fun main() {
    val count = 1800

    val warm: Koin = koinApplication { modules(multibindingModule(count)) }.koin
    warm.getAll<ScaleMarker>()
    warm.get<ScaleRoot>()

    val results = listOf(
        measure("t0_module_build_only", warmup = 5, reps = 20) {
            multibindingModule(count)
        },
        measure("t1_registration_only", warmup = 5, reps = 20) {
            koinApplication { modules(multibindingModule(count)) }.koin
        },
        measure("t2_registration_plus_realize", warmup = 5, reps = 20) {
            koinApplication { modules(multibindingModule(count)) }.koin.getAll<ScaleMarker>().size
        },
        measure("t3_getAll_pure_scan", warmup = 20, reps = 200) {
            warm.getAll<ScaleMarker>().size
        },
        measure("t4_get_warm_unqualified", warmup = 10_000, reps = 500_000) {
            warm.get<ScaleRoot>()
        },
        measure("t4b_get_warm_qualified", warmup = 10_000, reps = 500_000) {
            warm.get<ScaleContributor>(named("c0"))
        },
    )

    report(results)
    warm.close()
}
