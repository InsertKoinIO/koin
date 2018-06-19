package org.koin.test.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions

class DSLProviderTest : AutoCloseKoinTest() {

    val sampleModule = module {

        single { ComponentA() } bind MyInterface::class

        single { ComponentB() }

        factory { ComponentC() }
    }

    val sampleModule2 = module {
        single { ComponentA() as MyInterface }
    }

    interface MyInterface
    class ComponentA : MyInterface
    class ComponentB
    class ComponentC

    @Test
    fun `can create use several providers`() {
        startKoin(listOf(sampleModule))

        assertContexts(1)
        assertDefinitions(3)

        val a = get<ComponentA>()
        assertEquals(a, get<MyInterface>())

        val b = get<ComponentB>()
        assertEquals(b, get<ComponentB>())

        val c = get<ComponentB>()
        assertNotEquals(c, get<ComponentC>())
    }

    @Test
    fun `can create use bean for interface`() {
        startKoin(listOf(sampleModule2))

        assertContexts(1)
        assertDefinitions(1)

        val b = get<MyInterface>()
        assertEquals(b, get<MyInterface>())

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
        }
    }

}