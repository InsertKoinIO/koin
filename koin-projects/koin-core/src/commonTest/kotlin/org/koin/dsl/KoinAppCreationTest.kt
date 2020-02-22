package org.koin.dsl

import kotlin.test.*
import kotlin.test.assertEquals
import kotlin.test.Test
import org.koin.core.context.KoinContextHandler
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger
import org.koin.test.assertDefinitionsCount
import org.koin.test.assertHasNoStandaloneInstance

class KoinAppCreationTest {

    @AfterTest
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

        assertEquals(KoinContextHandler.get(), app.koin)

        stopKoin()

        assertHasNoStandaloneInstance()
    }

    @Test
    fun `can't restart a Koin application`() {
        startKoin { }
        try {
            startKoin { }
            fail("should throw  KoinAppAlreadyStartedException")
        } catch (e: IllegalStateException) {
        }
    }

    @Test
    fun `allow declare a logger`() {
        startKoin {
            logger(PrintLogger(Level.ERROR))
        }

        assertEquals(KoinContextHandler.get()._logger.level, Level.ERROR)

        KoinContextHandler.get()._logger.debug("debug")
        KoinContextHandler.get()._logger.info("info")
        KoinContextHandler.get()._logger.error("error")
    }

    @Test
    fun `allow declare a print logger level`() {
        startKoin {
            printLogger(Level.ERROR)
        }

        assertEquals(KoinContextHandler.get()._logger.level, Level.ERROR)

        KoinContextHandler.get()._logger.debug("debug")
        KoinContextHandler.get()._logger.info("info")
        KoinContextHandler.get()._logger.error("error")
    }
}