package org.koin.test.core

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.Koin
import org.koin.dsl.module.applicationContext
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions

class DSLProviderTest : AbstractKoinTest() {

    val sampleModule = applicationContext {

        provide { ComponentA() } bind MyInterface::class

        bean { ComponentB() }

        factory { ComponentC() }
    }

    val sampleModule2 = applicationContext {
        bean { ComponentA() as MyInterface }
    }

    interface MyInterface
    class ComponentA : MyInterface
    class ComponentB
    class ComponentC

    @Before
    fun before() {
        Koin.logger = PrintLogger()
    }

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