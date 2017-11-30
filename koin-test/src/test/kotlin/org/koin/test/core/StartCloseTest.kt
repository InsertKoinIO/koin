package org.koin.test.core

import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Test
import org.koin.Koin
import org.koin.error.AlreadyStartedException
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances


class StartCloseTest : KoinTest {

    init {
        Koin.logger = PrintLogger()
    }

    @Test
    fun `start and close Koin`() {
        startKoin(listOf())

        assertRemainingInstances(0)
        assertDefinitions(0)
        assertContexts(1)

        closeKoin()

        assertFalse(StandAloneContext.koinContext.isStarted())
    }

    @Test
    fun `start and restart Koin`() {
        startKoin(listOf())

        try {
            startKoin(listOf())
            fail()
        } catch (e: AlreadyStartedException) {
        }

        closeKoin()
    }

    @Test
    fun `start closed Koin`() {
        assertFalse(StandAloneContext.koinContext.isStarted())
    }
}