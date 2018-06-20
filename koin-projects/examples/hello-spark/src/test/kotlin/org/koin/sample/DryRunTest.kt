package org.koin.sample

import org.junit.After
import org.junit.Test
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.check
import org.koin.test.dryRun

/**
 * Dry run configuration
 */
class DryRunTest : KoinTest {

    @After
    fun after() {
        closeKoin()
    }

    @Test
    fun dryRunTest() {
        startKoin(listOf(helloAppModule))
        check()
        dryRun()
    }
}