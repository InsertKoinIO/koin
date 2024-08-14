package org.koin

import org.koin.core.context.stopKoin
import kotlin.test.AfterTest

abstract class KoinCoreTest {

    @AfterTest
    fun after() {
        stopKoin()
    }
}
