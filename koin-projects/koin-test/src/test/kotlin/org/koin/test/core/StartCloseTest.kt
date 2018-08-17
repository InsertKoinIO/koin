package org.koin.test.core

import org.junit.Assert.fail
import org.junit.Test
import org.koin.error.AlreadyStartedException
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances


class StartCloseTest : KoinTest {

    @Test
    fun `start and close Koin`() {
        startKoin(listOf())

        assertRemainingInstances(0)
        assertDefinitions(0)
        assertContexts(1)

        stopKoin()
    }

    @Test
    fun `start and restart Koin`() {
        startKoin(listOf())
        try {
            startKoin(listOf())
            fail()
        } catch (e: AlreadyStartedException) {
        }
        stopKoin()
    }
}