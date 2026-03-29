package org.koin.core

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.getScopeId
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import org.koin.mp.KoinPlatformTools
import org.koin.mp.Lockable
import org.koin.mp.generateId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConcurrencyTest {
    data class Counter(val id : String = KoinPlatformTools.generateId())
    class CounterService {

        private var counter = 0
        private val lock = Lockable()

        fun increment() = KoinPlatformTools.synchronized(lock) {
            counter++
        }

        fun getCounter(): Int = KoinPlatformTools.synchronized(lock) { counter }
    }

    val jobList = 100
    val jobRepeat = 100
    val scopeList = 10_000

    @Test
    fun parallel_access() = runTest {

        startKoin {
            modules(module {
                single { CounterService() }
            })
        }

        try {
            // Use a coroutine scope for concurrency
            coroutineScope {
                val jobs = List(jobList) {
                    launch {
                        repeat(jobRepeat) {
                            KoinPlatform.getKoin().get<CounterService>().increment()
                        }
                    }
                }
                jobs.joinAll()
            }

            assertEquals(jobList * jobRepeat, KoinPlatform.getKoin().get<CounterService>().getCounter())
        } finally {
            stopKoin()
        }
    }


    @OptIn(KoinInternalApi::class)
    @Test
    fun parallel_create_scopes() = runTest {

        startKoin {
            modules(module {
                scope<Counter>{
                    scoped { CounterService() }
                }
            })
        }

        val result = KoinPlatformTools.safeHashMap<String,Int>()

        try {
            coroutineScope {
                val startScopes = List(scopeList) {
                    launch {
                        KoinPlatform.getKoin().apply {
                            val counter = Counter()
                            val scope = createScope<Counter>(counter.getScopeId())
                            scope.get<CounterService>().increment()
                            result[scope.id] = scope.get<CounterService>().getCounter()
                            scope.close()
                        }
                    }
                }
                startScopes.joinAll()
            }
            assertTrue(KoinPlatform.getKoin().instanceRegistry.instances.values.none { it.isCreated() })
        } finally {
            stopKoin()
        }
        assertEquals(scopeList, result.size)
        assertTrue(result.values.all { it == 1 })
    }


    @OptIn(KoinInternalApi::class)
    @Test
    fun closed_flag_should_be_visible_across_coroutines_after_close() = runTest {
        // Verify that scope.closed is visible across threads after close() returns.
        // Without @Volatile on _closed, a write in close() on one thread may not be
        // visible to a read on another thread due to CPU cache staleness.
        //
        // Strategy: many coroutines on Dispatchers.Default (real threads) each create
        // a scope, close it, and then verify the closed flag is visible.

        startKoin {
            modules(module {
                scope<Counter> {
                    scoped { CounterService() }
                }
            })
        }

        val iterations = 10_000
        val staleReads = KoinPlatformTools.safeHashMap<String, Boolean>()

        try {
            coroutineScope {
                val jobs = List(iterations) { i ->
                    launch(kotlinx.coroutines.Dispatchers.Default) {
                        val koin = KoinPlatform.getKoin()
                        val counter = Counter()
                        val scope = koin.createScope<Counter>(counter.getScopeId())
                        scope.get<CounterService>()

                        // Close on this thread
                        scope.close()

                        // Immediately check visibility - on a different CPU core,
                        // without @Volatile this read may see stale false
                        if (!scope.closed) {
                            // Double-check after yield to give caches time
                            kotlinx.coroutines.yield()
                            if (!scope.closed) {
                                staleReads[scope.id] = true
                            }
                        }
                    }
                }
                jobs.joinAll()
            }
        } finally {
            stopKoin()
        }

        assertTrue(
            staleReads.isEmpty(),
            "Detected ${staleReads.size} stale reads of _closed flag out of $iterations iterations. " +
                "This indicates _closed is not safely published across threads - it should be @Volatile."
        )
    }

}
