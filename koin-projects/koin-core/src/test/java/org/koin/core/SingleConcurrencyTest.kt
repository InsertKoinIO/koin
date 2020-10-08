package org.koin.core

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class SingleConcurrencyTest {

    @Test
    fun `never creates two instances from different threads`() {
        val executor = Executors.newFixedThreadPool(8)
        // This test is repeated many times, because it only fails a very small % of times
        repeat(3000) { repetition ->
            val app = koinApplication {
                modules(module {
                    single { createComponent() }
                })
            }

            val numberOfInstances = (1..50)
                    .map { executor.submit(Callable { app.koin.get<Simple.ComponentA>() }) }
                    .map { it.get() }
                    .distinctBy { it.toString() }
                    .count()


            assertEquals(
                    "More than one instance created concurrently for a `single` definition. Failed in repetition $repetition of the test.",
                    1,
                    numberOfInstances,
            )
        }
    }

    private fun createComponent(): Simple.ComponentA {
        Thread.sleep(1) // Simulates a more expensive instance creation
        return Simple.ComponentA()
    }
}