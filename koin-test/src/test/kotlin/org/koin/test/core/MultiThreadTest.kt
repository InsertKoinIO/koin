package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class MultiThreadTest : AbstractKoinTest() {

    val module1 = applicationContext {
        bean { ComponentA() }
        bean { ComponentB(get()) }
    }

    val module2 = applicationContext {
        bean { ComponentA() }
        factory { ComponentB(get()) }
    }

    class ComponentA()
    class ComponentB(val componentA: ComponentA)

    @Test
    fun `multithread singleton`() {
        startKoin(listOf(module1))

        assertDefinitions(2)

        val values = arrayListOf<ComponentB>()

        (1..3).forEach {
            Thread().run {
                values += get<ComponentB>()
            }
        }

        while (values.size < 3) {
            Thread.sleep(100)
        }

        val default = get<ComponentB>()

        Assert.assertEquals(0, values.filter { it != default }.size)
        assertRemainingInstances(2)
        assertContexts(1)
    }

    @Test
    fun `multithread factory`() {
        startKoin(listOf(module2))

        assertDefinitions(2)

        val values = arrayListOf<ComponentB>()

        (1..3).forEach {
            Thread().run {
                values += get<ComponentB>()
            }
        }

        while (values.size < 3) {
            Thread.sleep(100)
        }

        val default = get<ComponentB>()

        Assert.assertEquals(3, values.filter { it != default }.size)
        assertRemainingInstances(1)
        assertContexts(1)
    }
}