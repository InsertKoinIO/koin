package org.koin.perfs

import org.junit.Test
import org.koin.core.time.logDuration
import org.koin.dsl.koinApplication
import org.koin.test.assertDefinitionsCount

class PerfsTest {

    @Test
    fun `empty module perfs`() {
        val app = logDuration("Koin app started") {
            koinApplication().start()
        }
        app.assertDefinitionsCount(0)
        app.stop()
    }

    @SuppressWarnings("unused")
    @Test
    fun `perfModule400 module perfs`() {
        val app = logDuration("Koin app started") {
            koinApplication {
                loadModules(perfModule400)
            }.start()
        }

        app.assertDefinitionsCount(400)

        val koin = app.koin

        logDuration("Requests") {
            koin.get<Perfs.A27>()
            koin.get<Perfs.B31>()
            koin.get<Perfs.C12>()
            koin.get<Perfs.D42>()
        }

        app.stop()
    }
}