package org.koin.perfs

import org.junit.Test
import org.koin.core.time.logDuration
import org.koin.dsl.koin
import org.koin.test.assertDefinitionsCount

class PerfsTest {

    @Test
    fun `empty module perfs`() {
        val app = logDuration("Koin app created") {
            koin().start()
        }

        app.assertDefinitionsCount(0)
        app.stop()
    }

    @SuppressWarnings("unused")
    @Test
    fun `perfModule400 module perfs`() {
        val app = logDuration("Koin app created") {
            koin {
                loadModules(perfModule400)
            }.start()
        }

        app.assertDefinitionsCount(400)

        val koin = app.koin

        koin.get<Perfs.A27>()
        koin.get<Perfs.B31>()
        koin.get<Perfs.C12>()
        koin.get<Perfs.D42>()

        app.stop()
    }
}