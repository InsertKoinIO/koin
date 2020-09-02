package org.koin.test

import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class DeclareTest : KoinTest {

    @Test
    fun `declare on the fly with KoinTest`() {
        startKoin {
            printLogger(Level.DEBUG)
        }

        try {
            get<Simple.ComponentA>()
            fail()
        } catch (e: Exception) {
        }

        loadKoinModules(
            module {
                single { Simple.ComponentA() }
            }
        )

        get<Simple.ComponentA>()

        stopKoin()
    }
}