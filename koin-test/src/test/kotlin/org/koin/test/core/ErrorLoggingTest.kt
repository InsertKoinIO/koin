package org.koin.test.core

import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AbstractKoinTest


class ErrorLoggingTest : AbstractKoinTest() {

    val module = applicationContext {
        bean { ComponentA() }
        bean { ComponentB(get()) }
    }

    class ComponentA {
        init {
            error("Boom !")
        }
    }

    class ComponentB(val a: ComponentA)

    @Test
    fun `should not inject generic interface component`() {
        startKoin(listOf(module))

        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should not inject generic interface component - linked dependency`() {
        startKoin(listOf(module))

        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}