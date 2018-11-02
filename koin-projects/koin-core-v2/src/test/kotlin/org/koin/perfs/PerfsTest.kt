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

    @Test
    fun `perfModule400 module perfs`() {
        val app = logDuration("Koin app created") {
            koin {
                loadModules(perfModule400)
            }.start()
        }

        app.assertDefinitionsCount(400)

        val koin = app.koin

        val a27: Perfs.A27 = koin.get()
        val b31: Perfs.B31 = koin.get()
        val c12: Perfs.C12 = koin.get()
        val d42: Perfs.D42 = koin.get()

        app.stop()
    }
}