package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.checkModules
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertIsInRootPath

class BadInstanceCreationTest : AutoCloseKoinTest() {

    val module1 = module {
        single { ComponentA() as MyInterface }
        single { ComponentB() } bind MyInterface::class
    }

    val module2 = module {
        single { ComponentA() } bind MyInterface::class
        single { ComponentB() } bind MyInterface::class
    }

    val module3 = module {
        single { ComponentC(get()) }
    }

    val module4 = module {
        single { ComponentD(get()) }
        single { ComponentE(get()) }
    }

    val module5 = module {
        single { ComponentError() }
    }

    val module6 = module {
        single { ComponentA() as MyInterface } bind MyInterface::class
    }

    interface MyInterface
    class ComponentA : MyInterface
    class ComponentB : MyInterface
    class ComponentC(val intf: MyInterface)
    class ComponentD(val componentE: ComponentE)
    class ComponentE(val componentD: ComponentD)
    class ComponentError() {
        init {
            error("Boum")
        }
    }

    @Test
    fun `can't create instance for MyInterface - one bind`() {
        startKoin(listOf(module1))

        val b = get<ComponentB>()

        try {
            get<MyInterface>()
            fail()
        } catch (e: Exception) {
            System.err.println(e)
        }

        Assert.assertNotNull(b)

        assertDefinitions(2)
        assertContexts(1)
        assertIsInRootPath(MyInterface::class)
        assertIsInRootPath(ComponentB::class)
    }

    @Test
    fun `can't create instance for MyInterface - two binds`() {
        startKoin(listOf(module2))

        val a = get<ComponentB>()
        val b = get<ComponentA>()

        try {
            get<MyInterface>()
            fail()
        } catch (e: Exception) {
            System.err.println(e)
        }

        Assert.assertNotNull(a)
        Assert.assertNotNull(b)

        assertDefinitions(2)
        assertContexts(1)
        assertIsInRootPath(ComponentA::class)
        assertIsInRootPath(ComponentB::class)
    }

    @Test
    fun `missing dependency`() {
        try {
            checkModules(listOf(module3))
            fail()
        } catch (e: Exception) {
            System.err.println(e)
        }
    }

    @Test
    fun `cyclic dependency`() {
        try {
            checkModules(listOf(module4))
            fail()
        } catch (e: Exception) {
            System.err.println(e)
        }
    }

    @Test
    fun `single internal error - checked`() {
        checkModules(listOf(module5))
    }

    @Test
    fun `multiple single definitions`() {
        checkModules(listOf(module6))
    }
}