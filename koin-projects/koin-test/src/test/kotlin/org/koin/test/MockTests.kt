package org.koin.test

import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.core.bean.BeanDefinition
import org.koin.core.parameter.emptyParametersHolder
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.mock.cloneForMock

class MockTests {

    @Test
    fun `mock an existing definition`() {
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
        println("-> $instance")
        val mock = mockedDefinition.instance.get<Simple.ComponentA>(parameters = { emptyParametersHolder() })
        println("-> $mock")

        assertNotEquals(instance, mock)
    }
}