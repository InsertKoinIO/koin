package org.koin.test

import org.junit.After
import org.koin.core.Koin
import org.koin.standalone.StandAloneContext.closeKoin

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

