package org.koin.core.registry

import org.koin.Simple
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.fail

// A first lookup racing a registry write must never leave the typed cache serving the replaced or
// unloaded definition. Writes skip the cache lock only until a container's first cache fill, so
// every round races on a fresh container.
class TypedIndexRaceTest {

    private class Round(val koin: Koin, val write: () -> Unit, val isConsistent: () -> Boolean)

    @Test
    fun get_racing_declare_override_resolves_the_latest_declaration() = raceFirstLookup {
        val koin = koinApplication { modules(module { single { Simple.ComponentA() } }) }.koin
        val latest = Simple.ComponentA()
        Round(
            koin,
            write = { koin.declare(latest, allowOverride = true) },
            isConsistent = { koin.get<Simple.ComponentA>() === latest },
        )
    }

    @Test
    fun get_racing_module_override_resolves_the_overriding_definition() = raceFirstLookup {
        val koin = koinApplication { modules(module { single { Simple.ComponentA() } }) }.koin
        val latest = Simple.ComponentA()
        Round(
            koin,
            write = { koin.loadModules(listOf(module { single { latest } }), allowOverride = true) },
            isConsistent = { koin.get<Simple.ComponentA>() === latest },
        )
    }

    @Test
    fun get_racing_module_unload_finds_no_definition_afterwards() = raceFirstLookup {
        // factory, not single: a single's first get() racing unload hits a separate, pre-existing
        // drop race ("Single instance created couldn't return value") that main has too.
        val loaded = module { factory { Simple.ComponentA() } }
        val koin = koinApplication { modules(loaded) }.koin
        Round(
            koin,
            write = { koin.unloadModules(listOf(loaded)) },
            isConsistent = { koin.getOrNull<Simple.ComponentA>() == null },
        )
    }

    private fun raceFirstLookup(newRound: () -> Round) {
        val current = AtomicReference<Round>()
        val roundNumber = AtomicInteger(0)
        val readerDone = AtomicInteger(0)
        val writerDone = AtomicInteger(0)
        val stop = AtomicBoolean(false)
        val workerError = AtomicReference<Throwable>()
        fun worker(done: AtomicInteger, action: (Round) -> Unit) = thread {
            var seen = 0
            while (!stop.get()) {
                val number = roundNumber.get()
                if (number == seen) { Thread.onSpinWait(); continue }
                seen = number
                try {
                    if (!stop.get()) action(current.get())
                } catch (e: Throwable) {
                    workerError.compareAndSet(null, e)
                }
                done.set(number)
            }
        }
        val reader = worker(readerDone) { it.koin.getOrNull<Simple.ComponentA>() }
        val writer = worker(writerDone) { it.write() }

        val deadline = System.nanoTime() + MAX_RACE_NANOS
        var failure: String? = null
        try {
            var i = 0
            while (failure == null && i < MAX_ROUNDS && System.nanoTime() < deadline) {
                i++
                val round = newRound()
                current.set(round)
                roundNumber.set(i)
                val roundDeadline = System.nanoTime() + ROUND_TIMEOUT_NANOS
                while (readerDone.get() != i || writerDone.get() != i) {
                    if (System.nanoTime() > roundDeadline) { failure = "round $i: a worker did not finish"; break }
                    Thread.onSpinWait()
                }
                workerError.get()?.let { throw AssertionError("round $i: worker failed", it) }
                if (failure == null && !round.isConsistent()) failure = "round $i: typed cache served a stale definition"
            }
        } finally {
            stop.set(true)
            roundNumber.incrementAndGet()
            reader.join(ROUND_TIMEOUT_MILLIS)
            writer.join(ROUND_TIMEOUT_MILLIS)
        }
        failure?.let { fail(it) }
    }

    private companion object {
        const val MAX_ROUNDS = 500_000
        const val MAX_RACE_NANOS = 8_000_000_000L
        const val ROUND_TIMEOUT_MILLIS = 5_000L
        const val ROUND_TIMEOUT_NANOS = ROUND_TIMEOUT_MILLIS * 1_000_000L
    }
}
