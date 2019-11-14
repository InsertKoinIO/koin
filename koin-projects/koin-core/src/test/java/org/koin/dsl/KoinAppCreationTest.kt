package org.koin.dsl

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger
import org.koin.test.assertDefinitionsCount
import org.koin.test.assertHasNoStandaloneInstance

class KoinAppCreationTest {

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun `make a Koin application`() {
        val app = koinApplication { }

        app.assertDefinitionsCount(0)

        assertHasNoStandaloneInstance()
    }

    @Test
    fun `start a Koin application`() {
        val app = startKoin { }

        assertEquals(GlobalContext.get(), app)

        stopKoin()

        assertHasNoStandaloneInstance()
    }

    @Test
    fun `can't restart a Koin application`() {
        startKoin { }
        try {
            startKoin { }
            fail("should throw  KoinAppAlreadyStartedException")
        } catch (e: KoinAppAlreadyStartedException) {
        }
    }

    @Test
    fun `allow declare a logger`() {
        startKoin {
            logger(PrintLogger(Level.ERROR))
        }

        assertEquals(KoinApplication.logger.level, Level.ERROR)

        KoinApplication.logger.debug("debug")
        KoinApplication.logger.info("info")
        KoinApplication.logger.error("error")
    }

    @Test
    fun `allow declare a print logger level`() {
        startKoin {
            printLogger(Level.ERROR)
        }

        assertEquals(KoinApplication.logger.level, Level.ERROR)

        KoinApplication.logger.debug("debug")
        KoinApplication.logger.info("info")
        KoinApplication.logger.error("error")
    }
}