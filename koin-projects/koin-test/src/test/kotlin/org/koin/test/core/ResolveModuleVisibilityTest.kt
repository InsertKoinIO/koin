package org.koin.test.core

import org.junit.Test
import org.koin.dsl.module.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.checkModules
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders

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
        checkModules(listOf(multipleWithModule))
        assertDefinitions(3)
        assertRemainingInstanceHolders(3)
    }

    @Test
    fun `should resolve only visibile definition - 2 defs with name`() {
        checkModules(listOf(multipleModule))
        assertDefinitions(3)
        assertRemainingInstanceHolders(3)
    }

    @Test
    fun `should resolve only parent`() {
        checkModules(listOf(multipleModules))
        assertDefinitions(3)
        assertRemainingInstanceHolders(3)
    }
}