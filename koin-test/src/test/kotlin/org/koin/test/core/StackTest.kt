package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.Koin
import org.koin.dsl.path.ModulePath
import org.koin.dsl.module.module
import org.koin.error.BeanInstanceCreationException
import org.koin.error.ContextVisibilityException
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertIsInModulePath
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertPath

class StackTest : AutoCloseKoinTest() {

    val FlatContextsModule = module {

        single { ComponentA() }

        module(path = "B") {
            single { ComponentB(get()) }
        }

        module(path = "C") {
            single { ComponentC(get()) }
        }
    }

    val HierarchyContextsModule = module {
        module(path = "A") {
            single { ComponentA() }

            module(path = "B") {
                single { ComponentB(get()) }

                module(path = "C") {
                    single { ComponentC(get()) }
                }
            }

        }
        single { ComponentD(get()) }
    }

    val NotVisibleContextsModule = module {

        single { ComponentB(get()) }

        module(path = "A") {
            single { ComponentA() }
        }

        module(path = "D") {
            single { ComponentD(get()) }
        }
    }

    class ComponentA
    class ComponentB(val componentA: ComponentA)
    class ComponentC(val componentA: ComponentA)
    class ComponentD(val componentB: ComponentB)


    @Test
    fun `has flat visibility`() {
        startKoin(listOf(FlatContextsModule))

        assertContexts(3)
        assertDefinitions(3)

        assertIsInModulePath(ComponentA::class, ModulePath.ROOT)
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")

        assertPath("B", ModulePath.ROOT)
        assertPath("C", ModulePath.ROOT)

        Assert.assertNotNull(get<ComponentC>())
        Assert.assertNotNull(get<ComponentB>())
        Assert.assertNotNull(get<ComponentA>())
    }

    @Test
    fun `has hierarchic visibility`() {
        startKoin(listOf(HierarchyContextsModule))

        Assert.assertNotNull(get<ComponentC>())
        Assert.assertNotNull(get<ComponentB>())
        Assert.assertNotNull(get<ComponentA>())
        try {
            get<ComponentD>()
            fail()
        } catch (e: BeanInstanceCreationException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `not good visibility context`() {
        Koin.logger = PrintLogger()
        startKoin(listOf(NotVisibleContextsModule))

        Assert.assertNotNull(get<ComponentA>())
        try {
            get<ComponentB>()
            fail()
        } catch (e: BeanInstanceCreationException) {
            e.printStackTrace()
        }
        try {
            get<ComponentD>()
            fail()
        } catch (e: ContextVisibilityException) {
            e.printStackTrace()
        }
    }

}