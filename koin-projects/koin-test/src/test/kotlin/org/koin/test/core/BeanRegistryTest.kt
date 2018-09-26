package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest

/**
 * Created by Nick Cipollo on 9/26/18.
 */
class BeanRegistryTest : AutoCloseKoinTest() {

    @Test
    fun `lots of beans`() {
        val module = module {
            (0..100000L).forEach { index ->
                single(name = index.toString()) { index }
            }
        }

        StandAloneContext.startKoin(listOf(module), logger = PrintLogger(true))

        (0..100L).forEach {
            Assert.assertEquals(it, get(it.toString()))
        }
    }
}
