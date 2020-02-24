package org.koin.test

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.test.mock.declareModule
import kotlin.test.Test
import kotlin.test.fail

class DeclareTest : AutoCloseKoinTest() {

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

        declareModule {
            single { Simple.ComponentA() }
        }

        get<Simple.ComponentA>()

        stopKoin()
    }
}