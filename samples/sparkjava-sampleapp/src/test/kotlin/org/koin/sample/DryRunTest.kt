package org.koin.sample

import org.junit.Test
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest
import org.koin.test.dryRun

/**
 * Dry run configuration
 */
class DryRunTest : KoinTest {

    @Test
    fun dryRunTest() {
        startKoin(listOf(helloAppModule))
        dryRun()
        closeKoin()
    }
}