package org.koin.test.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AbstractKoinTest


class GenericBindingTest : AbstractKoinTest() {

    val module = applicationContext {
        bean("a") { ComponentA() as InterfaceComponent<String> }
        bean("b") { ComponentB() as InterfaceComponent<Int> }

    }

    val badModule = applicationContext {
        bean { ComponentA() as InterfaceComponent<String> }
        bean { ComponentB() as InterfaceComponent<Int> }

    }

    interface InterfaceComponent<T>
    class ComponentA : InterfaceComponent<String>
    class ComponentB : InterfaceComponent<Int>

    @Test
    fun `should inject generic interface component`() {
        startKoin(listOf(module))

        val a = get<InterfaceComponent<String>>("a")
        assertTrue(a is ComponentA)
    }

    @Test
    fun `should not inject generic interface component`() {
        startKoin(listOf(badModule))

        val a = get<InterfaceComponent<String>>()
        // Bean has been overridden
        assertFalse(a is ComponentA)
    }
}