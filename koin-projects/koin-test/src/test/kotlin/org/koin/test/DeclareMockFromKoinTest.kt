package org.koin.test

import org.junit.Assert
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito

class DeclareMockFromKoinTest : KoinTest {

    val mock: Simple.UUIDComponent by inject()

    @Test
    fun `declareMock with KoinTest`() {
        startKoin {
            logger(Level.DEBUG)
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
}