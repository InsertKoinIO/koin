package org.koin.test

import org.junit.After
import org.koin.Koin
import org.koin.KoinContext
import org.koin.ParameterMap
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
fun KoinTest.dryRun(defaultParameters: ParameterMap = emptyMap()) {
    (StandAloneContext.koinContext as KoinContext).dryRun(defaultParameters)
}

/**
 * Koin Test - embed autoclose @after method to close Koin after every test
 */
abstract class AutoCloseKoinTest() : KoinTest {

    @After
    fun autoClose() {
        Koin.logger.log("AutoClose Koin")
        closeKoin()
    }
}

