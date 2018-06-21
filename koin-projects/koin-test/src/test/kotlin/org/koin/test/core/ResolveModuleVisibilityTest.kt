package org.koin.test.core

import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.check
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class ResolveModuleVisibilityTest : AutoCloseKoinTest() {

    val multipleWithModule = module {
        single { ComponentC() as Component }
        module("otherModule") {
            single { ComponentC() as Component }
        }
        single { ComponentD(get()) }
    }

    val multipleModule = module {
        single("default") { ComponentC() as Component }
        single("other") { ComponentC() as Component }
        single { ComponentD(get("default")) }
    }

    val multipleModules = module {
        single { ComponentC() as Component }
        module("otherModule") {
            single { ComponentC() as Component }
        }
        module("module") {
            single { ComponentD(get()) }
        }
    }

    class ComponentA()
    class ComponentB(val componentA: ComponentA)

    interface Component
    class ComponentC() : Component
    class ComponentD(val component: Component)

    @Test
    fun `should resolve only visibile definition - 2 defs one module`() {
        check(listOf(multipleWithModule))
        assertDefinitions(3)
        assertRemainingInstances(3)
    }

    @Test
    fun `should resolve only visibile definition - 2 defs with name`() {
        check(listOf(multipleModule))
        assertDefinitions(3)
        assertRemainingInstances(3)
    }

    @Test
    fun `should resolve only parent`() {
        check(listOf(multipleModules))
        assertDefinitions(3)
        assertRemainingInstances(3)
    }
}