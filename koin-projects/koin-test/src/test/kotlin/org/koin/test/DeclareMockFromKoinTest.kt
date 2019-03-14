package org.koin.test

import org.junit.Assert
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito
import java.util.*

class DeclareMockFromKoinTest : AutoCloseKoinTest() {

    val mock: Simple.UUIDComponent by inject()

    @Test
    fun `declareMock with KoinTest`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        single { Simple.UUIDComponent() }
                    }
            )
        }

        val uuidValue = "UUID"
        declareMock<Simple.UUIDComponent> {
            BDDMockito.given(getUUID()).will { uuidValue }
        }

        Assert.assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    fun `declareMock factory with KoinTest`() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                    module {
                        factory { Simple.UUIDComponent() }
                    }
            )
        }

        declareMock<Simple.UUIDComponent> {
            BDDMockito.given(getUUID()).will { UUID.randomUUID().toString() }
        }

        val val1 = getKoin().get<Simple.UUIDComponent>().getUUID()
        val val2 = getKoin().get<Simple.UUIDComponent>().getUUID()

        assertNotEquals(val1, val2)
    }
}