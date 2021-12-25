package org.koin.core.state

import org.koin.core.qualifier.named
import org.koin.core.runBackground
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.*

class PlatformThreadingTest {
    val scopeKey = named("KEY")
    val koin = koinApplication {
        modules(
                module {
                    scope(scopeKey) {
                    }
                }
        )
    }.koin

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun scopeInBackgroundFails() {
        val theException = runBackground {
            var throwable: Throwable? = null
            try {
                koin.createScope("myScope", scopeKey)
            } catch (e: Exception) {
                throwable = e
            }

            throwable
        }

        theException?.printStackTrace()
        if (isExperimentalMM()) {
            assertNull(theException)
        } else {
            assertNotNull(theException)
        }
    }
}