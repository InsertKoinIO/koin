package org.koin.test.junit5

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.Simple
import org.koin.test.inject
import org.koin.test.junit5.mock.MockProviderExtension
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.util.*

class DeclareMockFromKoinTest : AutoCloseKoinTest() {

    @JvmField
    @RegisterExtension
    val mockProvider = MockProviderExtension.create { clazz ->
        Mockito.mock(clazz.java)
    }

    val mock: Simple.UUIDComponent by inject()

    @Test
    @DisplayName("declareMock with koinTest")
    fun declaresMockForTestingContext() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                module {
                    single { Simple.UUIDComponent() }
                },
            )
        }

        val uuidValue = "UUID"
        declareMock<Simple.UUIDComponent> {
            BDDMockito.given(getUUID()).will { uuidValue }
        }

        Assertions.assertEquals(uuidValue, mock.getUUID())
    }

    @Test
    @DisplayName("declareMock factory with KoinTest")
    fun declareMockWhenUsingFactory() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(
                module {
                    factory { Simple.UUIDComponent() }
                },
            )
        }

        declareMock<Simple.UUIDComponent> {
            BDDMockito.given(getUUID()).will { UUID.randomUUID().toString() }
        }

        val val1 = getKoin().get<Simple.UUIDComponent>().getUUID()
        val val2 = getKoin().get<Simple.UUIDComponent>().getUUID()

        Assertions.assertNotEquals(val1, val2)
    }
}
