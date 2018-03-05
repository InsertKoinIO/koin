package org.koin.test.core

import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest


class ErrorLoggingTest : AutoCloseKoinTest() {

    val module = applicationContext {
        bean { ComponentA() }
        bean { ComponentB(get()) }
    }

    val cyclicModule = applicationContext {
        bean { ComponentAA(get()) }
        bean { ComponentAB(get()) }
    }

    class ComponentA {
        init {
            error("Boom !")
        }
    }

    class ComponentB(val a: ComponentA)

    class ComponentAA(val componentAB: ComponentAB)
    class ComponentAB(val componentAA: ComponentAA)

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

    @Test
    fun `should not inject cyclic deps`() {
        startKoin(listOf(cyclicModule))

        try {
            get<ComponentAA>()
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}