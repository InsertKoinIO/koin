package org.koin.test.core

import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.check
import org.koin.test.dryRun
import org.koin.test.error.BrokenDefinitionException
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstances

class CheckTest : AutoCloseKoinTest() {

    val simpleModule = module {
        single { ComponentA() }
        single { ComponentB(get()) }
    }

    val brokenModule = module {
        single { ComponentB(get()) }
    }

    val interfaceModule = module {
        single { ComponentC() as Component }
        single { ComponentD(get()) }
    }

    val multipleInterfaceModule = module {
        single { ComponentC() as Component }
        module("otherModule") {
            single { ComponentC() as Component }
        }
        single { ComponentD(get()) }
    }

    val multipleInterfaceModule2 = module {
        single("default") { ComponentC() as Component }
        single("other") { ComponentC() as Component }
        single { ComponentD(get("default")) }
    }

    val module1 = module {
        module("otherModule") {
            single { ComponentC() as Component }
        }
        single { ComponentD(get()) }
    }

    val module2 = module {
        single { ComponentC() as Component }
    }

    class ComponentA()
    class ComponentB(val componentA: ComponentA)

    interface Component
    class ComponentC() : Component
    class ComponentD(val component: Component)

    @Test
    fun `successful check`() {
        startKoin(listOf(simpleModule))
        check()

        assertDefinitions(2)
        assertRemainingInstances(0)

        dryRun()
    }

    @Test
    fun `unsuccessful check`() {
        try {
            startKoin(listOf(brokenModule))
            check()
            fail()
        } catch (e: BrokenDefinitionException) {
            System.err.println(e)
        }

        assertDefinitions(1)
        assertRemainingInstances(0)
    }

    @Test
    fun `interface definition check`() {
        startKoin(listOf(interfaceModule))
        check()

        assertDefinitions(2)
        assertRemainingInstances(0)

        dryRun()
    }

    @Test
    fun `multiple interface & module definition check`() {
        startKoin(listOf(multipleInterfaceModule))
        check()

        assertDefinitions(3)
        assertRemainingInstances(0)

        dryRun()
    }

    @Test
    fun `mutiple module defs - check`() {
        startKoin(listOf(module1,module2))
        check()

        assertDefinitions(3)
        assertRemainingInstances(0)

        dryRun()
    }

    @Test
    fun `multiple interface definition check`() {
        startKoin(listOf(multipleInterfaceModule2))
        check()

        assertDefinitions(3)
        assertRemainingInstances(0)

        dryRun()
    }
}