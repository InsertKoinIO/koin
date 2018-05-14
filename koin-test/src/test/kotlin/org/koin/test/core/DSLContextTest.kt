package org.koin.test.core

import org.junit.Test
import org.koin.dsl.path.ModulePath
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertIsInModulePath
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertPath

class DSLContextTest : AutoCloseKoinTest() {

    val FlatContextsModule = module {

        single { ComponentA() }

        module(path = "B") {
            single { ComponentB() }
        }

        module(path = "C") {
            single { ComponentC() }
        }
    }

    val HierarchyContextsModule = module {
        module(path = "A") {
            single { ComponentA() }

            module(path = "B") {
                single { ComponentB() }

                module(path = "C") {
                    single { ComponentC() }
                }
            }
        }
    }

    class ComponentA
    class ComponentB
    class ComponentC

    @Test
    fun `can create flat contexts`() {
        startKoin(listOf(FlatContextsModule))

        assertContexts(3)
        assertDefinitions(3)

        assertIsInModulePath(ComponentA::class, ModulePath.ROOT)
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")

        assertPath("B", ModulePath.ROOT)
        assertPath("C", ModulePath.ROOT)
    }

    @Test
    fun `can create hierarchic contexts`() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertIsInModulePath(ComponentA::class, "A")
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")

        assertPath("A", ModulePath.ROOT)
        assertPath("B", "A")
        assertPath("C", "B")
    }

}