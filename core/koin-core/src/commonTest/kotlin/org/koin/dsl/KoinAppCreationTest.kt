package org.koin.dsl

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.logger.Level
import org.koin.mp.PlatformTools
import org.koin.test.assertDefinitionsCount
import org.koin.test.assertHasNoStandaloneInstance
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

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

        assertEquals(PlatformTools.defaultContext().get(), app.koin)

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
            logger(PlatformTools.defaultLogger(Level.ERROR))
        }

        assertEquals(PlatformTools.defaultContext().get().logger.level, Level.ERROR)

        PlatformTools.defaultContext().get().logger.debug("debug")
        PlatformTools.defaultContext().get().logger.info("info")
        PlatformTools.defaultContext().get().logger.error("error")
    }

//    @Test
//    fun `allow declare a print logger level`() {
//        startKoin {
//            printLogger(Level.ERROR)
//        }
//
//        assertEquals(PlatformTools.defaultContext().get().logger.level, Level.ERROR)
//
//        PlatformTools.defaultContext().get().logger.debug("debug")
//        PlatformTools.defaultContext().get().logger.info("info")
//        PlatformTools.defaultContext().get().logger.error("error")
//    }
}