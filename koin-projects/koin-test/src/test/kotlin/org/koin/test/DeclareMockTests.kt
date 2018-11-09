package org.koin.test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.core.bean.BeanDefinition
import org.koin.core.logger.Level
import org.koin.core.parameter.emptyParametersHolder
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.mock.cloneForMock
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given

@Suppress("UNCHECKED_CAST")
class DeclareMockTests : KoinTest {

    @Test
    fun `create mock of an existing definition`() {
        val koin = koinApplication {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                }
            )
        }.koin

        val definition: BeanDefinition<Simple.ComponentA> =
            koin.beanRegistry.findDefinition(
                null, Simple.ComponentA::class
            ) as BeanDefinition<Simple.ComponentA>


        val mockedDefinition = definition.cloneForMock()

        val instance = definition.instance.get<Simple.ComponentA>(parameters = { emptyParametersHolder() })
        val mock = mockedDefinition.instance.get<Simple.ComponentA>(parameters = { emptyParametersHolder() })

        assertNotEquals(instance, mock)
    }

    @Test
    fun `declare a Mock of an existing definition`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(
                module {
                    single { Simple.ComponentA() }
                }
            )
        }.koin

        val instance = koin.get<Simple.ComponentA>()

        koin.declareMock<Simple.ComponentA>()

        val mock = koin.get<Simple.ComponentA>()

        assertNotEquals(instance, mock)
    }

    @Test
    fun `declare and mock an existing definition`() {
        val koin = koinApplication {
            useLogger(Level.DEBUG)
            loadModules(
                module {
                    single { Simple.UUIDComponent() }
                }
            )
        }.koin

        val instance = koin.get<Simple.UUIDComponent>()
        val uuidValue = "UUID"

        koin.declareMock<Simple.UUIDComponent> {
            given(getUUID()).will { uuidValue }
        }

        val mock = koin.get<Simple.UUIDComponent>()

        assertNotEquals(instance, mock)
        assertEquals(uuidValue, mock.getUUID())
    }
}