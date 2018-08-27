package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.declareMock
import org.koin.test.declare
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders
import org.mockito.Mockito.mock

class CreateMocksAndDeclareTest : AutoCloseKoinTest() {

    interface InterfaceA
    class ComponentA() : InterfaceA
    class ComponentB(val componentA: ComponentA)

    @Test
    fun `successful override with an interface mock`() {
        startKoin(listOf(
            module {
                single { ComponentA() } bind InterfaceA::class
                single { ComponentB(get()) }
            }
        ))

        declareMock<ComponentA>(binds = listOf(InterfaceA::class))

        val mockA = get<InterfaceA>()
        Assert.assertEquals(mockA, get<ComponentB>().componentA)

        assertDefinitions(2)
        assertRemainingInstanceHolders(2)
    }

    @Test
    fun `successful create a mock`() {
        startKoin(listOf())

        declareMock<ComponentA>()

        Assert.assertNotNull(get<ComponentA>())

        assertDefinitions(1)
        assertRemainingInstanceHolders(1)
    }

    @Test
    fun `successful override with a mock`() {
        startKoin(listOf(
            module {
                single { ComponentA() }
                single { ComponentB(get()) }
            }
        ))

        declareMock<ComponentA>()

        val mockA = get<ComponentA>()
        Assert.assertEquals(mockA, get<ComponentB>().componentA)

        assertDefinitions(2)
        assertRemainingInstanceHolders(2)
    }

    @Test
    fun `successful override factory with a mock`() {
        startKoin(listOf(module {
            factory { ComponentA() }
        }))

        declareMock<ComponentA>(isFactory = true)

        Assert.assertNotEquals(get<ComponentA>(), get<ComponentA>())

        assertDefinitions(1)
        assertRemainingInstanceHolders(1)
    }

    @Test
    fun `successful declare an expression mock`() {
        startKoin(listOf())

        declare { factory { mock(ComponentA::class.java) } }

        Assert.assertNotEquals(get<ComponentA>(), get<ComponentA>())

        assertDefinitions(1)
        assertRemainingInstanceHolders(1)
    }
}