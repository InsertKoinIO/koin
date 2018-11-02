package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.error.KoinAlreadyStartedException
import org.koin.core.standalone.StandAloneKoinApplication
import org.koin.test.assertDefinitionsCount
import org.koin.test.assertHasNoStandaloneInstance

class KoinAppCreationTest {

    @Test
    fun `make a Koin application`() {
        val app = koinApplication()

        app.assertDefinitionsCount(0)

        assertHasNoStandaloneInstance()
    }

    @Test
    fun `start a Koin application`() {
        val app = koinApplication().start()

        assertEquals(StandAloneKoinApplication.get(), app)

        app.stop()

        assertHasNoStandaloneInstance()
    }

    @Test
    fun `can't restart a Koin application`() {
        val app = koinApplication().start()
        try {
            app.start()
            fail("should throw  KoinAlreadyStartedException")
        } catch (e: KoinAlreadyStartedException) {
        }
        app.stop()
    }
}