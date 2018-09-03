package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders

class MultiThreadTest : AutoCloseKoinTest() {

    val module1 = module {
        single { ComponentA() }
        single { ComponentB(get()) }
    }

    val module2 = module {
        single { ComponentA() }
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
        assertRemainingInstanceHolders(2)
        assertContexts(1)
    }

    @Test
    fun `multithread singleton alt time`() {
        startKoin(listOf(module1))

        assertDefinitions(2)

        val values = arrayListOf<ComponentB>()

        (1..3).forEach { i ->
            Thread().run {
                Thread.sleep(1000L / i)
                println("$i get B")
                values += get<ComponentB>()
            }
        }

        while (values.size < 3) {
            Thread.sleep(100)
        }

        val default = get<ComponentB>()

        Assert.assertEquals(0, values.filter { it != default }.size)
        assertRemainingInstanceHolders(2)
        assertContexts(1)
    }

    @Test
    fun `multithread singleton lots threads`() {
        startKoin(listOf(module1))

        assertDefinitions(2)

        val values = arrayListOf<ComponentB>()

        (1..100).forEach { i ->
            Thread().run {
                println("$i get B")
                values += get<ComponentB>()
            }
        }

        while (values.size < 3) {
            Thread.sleep(100)
        }

        val default = get<ComponentB>()

        Assert.assertEquals(0, values.filter { it != default }.size)
        assertRemainingInstanceHolders(2)
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
        assertRemainingInstanceHolders(2)
        assertContexts(1)
    }

    @Test
    fun `multithread factory lots threads`() {
        startKoin(listOf(module2))

        assertDefinitions(2)

        val values = arrayListOf<ComponentB>()

        val size = 100
        (1..size).forEach { i ->
            Thread().run {
                println("$i get B")
                values += get<ComponentB>()
            }
        }

        while (values.size < size) {
            Thread.sleep(100L)
        }

        val default = get<ComponentB>()
        Assert.assertEquals(size, values.filter { it != default }.size)
        assertRemainingInstanceHolders(2)
        assertContexts(1)
    }
}