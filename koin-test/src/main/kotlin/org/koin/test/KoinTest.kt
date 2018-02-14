package org.koin.test

import org.junit.After
import org.koin.Koin
import org.koin.KoinContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.closeKoin

/**
 * Koin Test Component
 */
interface KoinTest : KoinComponent

/**
 * Make a Dry Run - Test if each definition is injectable
 */
fun KoinTest.dryRun() {
    (StandAloneContext.koinContext as KoinContext).dryRun()
}

/**
 * Koin Test - embed autoclose @after method to close Koin after every test
 */
abstract class AutoCloseKoinTest(val autoCloseKoin: Boolean = true) : KoinTest {
    @After
    fun autoClose() {
        if (autoCloseKoin) {
            Koin.logger.log("AutoClose Koin")
            closeKoin()
        }
    }
}

