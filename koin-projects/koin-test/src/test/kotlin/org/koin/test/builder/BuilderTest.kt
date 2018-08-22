package org.koin.test.builder

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.error.BeanInstanceCreationException
import org.koin.experimental.builder.create
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertRemainingInstances

class BuilderTest : AutoCloseKoinTest() {

    class ComponentA
    class ComponentB(val a: ComponentA)
    class ComponentC(val a: ComponentA, val b: ComponentB)

    interface Component
    class ComponentD(val a: ComponentA) : Component

    @Test
    fun `should find 1st constructor and build`() {
        startKoin(listOf(module {
            single { ComponentA() }
            single { create<ComponentB>() }
            single { create<ComponentC>() }
        }))

        assertRemainingInstances(0)

        assertNotNull(get<ComponentB>())
        assertNotNull(get<ComponentB>())
        assertNotNull(get<ComponentC>())

        assertRemainingInstances(3)
    }

    @Test
    fun `should not dependency`() {
        startKoin(listOf(module {
            single { ComponentA() }
            single { create<ComponentC>() }
        }))

        try {
            assertNotNull(get<ComponentC>())
        } catch (e: BeanInstanceCreationException) {
        }
    }

    @Test
    fun `should get instance - empty ctor`() {
        startKoin(listOf(module {
            single { create<ComponentA>() }
        }))

        assertNotNull(get<ComponentA>())
    }

    @Test
    fun `should get interface instance`() {
        startKoin(listOf(module {
            single { ComponentA() }
            single { create<ComponentD>() as Component }
        }))

        assertNotNull(get<Component>())
        assertRemainingInstances(2)
    }

}