package org.koin.core

import org.koin.Errors
import org.koin.core.logger.Level
import org.koin.core.mp.KoinMultiPlatform
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.fail

class StackOverflowErrorTest {
    @Test
    fun `cycle error`() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                single { Errors.CycleA(get()) }
                single { Errors.CycleB(get()) }
            })
        }

        try {
            app.koin.get<Errors.CycleA>()
            fail("should break into cycle")
        } catch (e: StackOverflowError) {
            KoinMultiPlatform.printStackTrace(e)
        }
    }
}
