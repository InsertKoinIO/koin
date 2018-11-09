package org.koin.test

import org.junit.Assert
import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito

class DeclareMockFromKoinTest : KoinTest {

    val mock: Simple.UUIDComponent by inject()

    @Test
    fun `declareMock with KoinTest`() {
        koinApplication {
            useLogger(Level.DEBUG)
            loadModules(
                module {
                    single { Simple.UUIDComponent() }
                }
            )
        }.start()

        val uuidValue = "UUID"
        declareMock<Simple.UUIDComponent> {
            BDDMockito.given(getUUID()).will { uuidValue }
        }

        Assert.assertEquals(uuidValue, mock.getUUID())
    }
}