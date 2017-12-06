package org.koin.sample

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.log.PrintLogger
import org.koin.sample.Property.WHO
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.dryRun

/**
 * Dry run configuration
 */
class DryRunTest : KoinTest {

    @Before
    fun before() {
        Koin.logger = PrintLogger()
    }

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun dryRunTest() {
        startKoin(listOf(HelloModule), properties = mapOf(WHO to "TEST"))
        dryRun()
    }
}