package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.assertNotEquals
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.dsl.path.path
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class ClassPathTest : AutoCloseKoinTest() {

    val module = module {
        module(ComponentB::class.path()) {
            single { ComponentA() }
            single { ComponentB(get()) }
        }

        module(ComponentC::class.path()) {
            single { ComponentA() }
            single { ComponentC(get()) }
        }
    }

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA)

    @Test
    fun `declared module from classes`() {
        startKoin(listOf(module))

        assertDefinitions(4)

        Assert.assertNotNull(get<ComponentB>())
        Assert.assertNotNull(get<ComponentC>())

        val a_b = get<ComponentA>(module = ComponentB::class.path())
        val a_c = get<ComponentA>(module = ComponentC::class.path())
        assertNotEquals(a_b, a_c)

        assertRemainingInstances(4)
    }

    @Test
    fun `fail - declared module from classes`() {
        startKoin(listOf(module))

        get<ComponentB>()
        try {
            get<ComponentB>(module = ComponentC::class.path())
            fail()
        } catch (e: Exception) {
        }

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
        }
    }
}