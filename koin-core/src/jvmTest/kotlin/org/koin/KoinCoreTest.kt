package org.koin

import org.junit.After
import org.koin.core.context.stopKoin

abstract class KoinCoreTest {

    @After
    fun after() {
        stopKoin()
    }

}