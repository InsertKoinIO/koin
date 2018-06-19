package org.koin.test.core

import org.junit.Test
import org.koin.dsl.module.module
import org.koin.dsl.path.Path
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.*

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

        assertIsInRootPath(ComponentA::class)
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")

        assertPath("B", Path.ROOT)
        assertPath("C", Path.ROOT)
    }

    @Test
    fun `can create hierarchic contexts`() {
        startKoin(listOf(HierarchyContextsModule))

        assertContexts(4)
        assertDefinitions(3)

        assertIsInModulePath(ComponentA::class, "A")
        assertIsInModulePath(ComponentB::class, "B")
        assertIsInModulePath(ComponentC::class, "C")

        assertPath("A", Path.ROOT)
        assertPath("B", "A")
        assertPath("C", "B")
    }

}