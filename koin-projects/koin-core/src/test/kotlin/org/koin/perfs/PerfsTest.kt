package org.koin.perfs

import org.junit.Test
import org.koin.core.time.measureDuration
import org.koin.dsl.koinApplication
import org.koin.test.assertDefinitionsCount

class PerfsTest {

    @Test
    fun `empty module perfs`() {
        val (app, duration) = measureDuration {
            koinApplication {
            }
        }
        println("started in $duration ms")

        app.assertDefinitionsCount(0)
        app.close()
    }

    /*
    Perfs on MBP 2018
        1st run -
            [INFO] [Koin] load modules in 19.351227 ms
            started in 178.637936 ms
            measured executed in 1.02544 ms
        2nd run -
            [INFO] [Koin] load modules in 4.122111 ms
            started in 4.175906 ms
            measured executed in 0.042183 ms
     */
    @Test
    fun `perfModule400 module perfs`() {
        runPerfs()
        runPerfs()
    }

    private fun runPerfs() {
        val (app, duration) = measureDuration {
            koinApplication {
                printLogger()
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