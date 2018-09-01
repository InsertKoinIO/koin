package org.koin.test.module

import org.junit.Assert
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders

class ImplicitNamingTest : AutoCloseKoinTest() {

    val module = module {
        module("B") {
            single { ComponentA() }
            single { ComponentB(get()) }
        }

        module("C") {
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

        val a_b = get<ComponentA>(name = "B.ComponentA")
        val a_c = get<ComponentA>(name = "C.ComponentA")
        assertNotEquals(a_b, a_c)

        assertRemainingInstanceHolders(4)
    }
}