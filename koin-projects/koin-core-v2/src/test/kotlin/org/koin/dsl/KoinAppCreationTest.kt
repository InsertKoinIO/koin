package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.standalone.StandAloneContext
import org.koin.test.assertDefinitionsCount
import org.koin.test.assertHasNoStandaloneInstance

class KoinAppCreationTest {

    @Test
    fun `start a Koin application instance`() {
        val app = koin()

        app.assertDefinitionsCount(0)

        assertHasNoStandaloneInstance()
    }

    @Test
    fun `start a Koin application standalone instance`() {
        val app = koin().start()

        assertEquals(StandAloneContext.getKoin(), app)

        app.stop()

        assertHasNoStandaloneInstance()
    }
}