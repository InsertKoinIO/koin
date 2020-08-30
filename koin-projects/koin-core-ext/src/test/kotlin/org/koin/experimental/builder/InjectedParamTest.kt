package org.koin.experimental.builder

import org.junit.Assert
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class MySingle(val id: Int)

class InjectedParamTest {

    @Test
    fun `params injection`() {
        val app = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single<MySingle>()
                })
        }

        val koin = app.koin
        val a: MySingle = koin.get { parametersOf(42) }

        Assert.assertEquals(42, a.id)
    }
}