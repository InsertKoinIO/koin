package org.koin.perfs

import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.time.measureDuration
import org.koin.dsl.koinApplication
import org.koin.test.assertDefinitionsCount

class PerfsTest {

    @Test
    fun `empty module perfs`() {
        val (app, duration) = measureDuration {
            koinApplication {
                logger()
            }
        }
        println("started in $duration ms")

        app.assertDefinitionsCount(0)
        app.close()
    }

    @SuppressWarnings("unused")
    @Test
    fun `perfModule400 module perfs`() {
        val (app, duration) = measureDuration {
            koinApplication {
                logger()
                modules(perfModule400)
            }
        }
        println("started in $duration ms")

        app.assertDefinitionsCount(400)

        val koin = app.koin

        val (_, executionDuration) = measureDuration {
            koin.get<Perfs.A27>()
            koin.get<Perfs.B31>()
            koin.get<Perfs.C12>()
            koin.get<Perfs.D42>()
        }
        println("measured executed in $executionDuration ms")

        app.close()
    }
}