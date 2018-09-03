package org.koin.sample

import org.junit.After
import org.junit.Test
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.checkModules

/**
 * Dry run configuration
 */
class DryRunTest : KoinTest {

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun dryRunTest() {
        checkModules(listOf(helloAppModule))
    }
}