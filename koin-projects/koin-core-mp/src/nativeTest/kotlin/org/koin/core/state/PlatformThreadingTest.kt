package org.koin.core.state

import co.touchlab.testhelp.concurrency.background
import org.koin.core.qualifier.named
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

    @Test
    fun scopeInBackgroundFails() {
        val theException = background {
            var throwable:Throwable? = null
            try {
                koin.createScope("myScope", scopeKey)
            } catch (e: Exception) {
                throwable = e
            }

            throwable
        }

        theException?.printStackTrace()
        assertNotNull(theException)
        assertEquals("Must be main thread", theException?.message)
    }

    @Test
    fun scopeInMainOk() {
        koin.createScope("myScope", scopeKey)
    }

    @Test
    fun checkPlatformThreading(){
        assertTrue(isMainThread)
        background {
            assertFalse(isMainThread)
        }
    }
}