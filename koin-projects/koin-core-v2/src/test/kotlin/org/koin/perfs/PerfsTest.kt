package org.koin.perfs

import org.junit.Test
import org.koin.core.time.logDuration
import org.koin.dsl.koin
import org.koin.test.assertDefinitionsCount

class PerfsTest {

    @Test
    fun `load perfs empty module`() {
        val app = logDuration("Koin app created"){
            koin().start()
        }

        app.assertDefinitionsCount(0)
        app.stop()
    }

    @Test
    fun `load perfs module`() {
        val app = logDuration("Koin app created"){
            koin {
                loadModules(perfModule400)
            }.start()
        }

        app.assertDefinitionsCount(400)
        app.stop()
    }
}