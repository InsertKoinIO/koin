package org.koin.test

import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.fail

class DeclareTest : KoinTest {

    @Test
    fun declare_on_the_fly_with_KoinTest() {
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
            },
        )

        get<Simple.ComponentA>()

        stopKoin()
    }
}
