package org.koin.sample

import org.junit.After
import org.junit.Test
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.test.KoinTest
import org.koin.test.check

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
        check(listOf(helloAppModule))
    }
}