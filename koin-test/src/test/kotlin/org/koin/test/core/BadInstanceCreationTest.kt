package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.dryRun
import org.koin.test.ext.junit.assertContexts
import org.koin.test.ext.junit.assertDefinedInScope
import org.koin.test.ext.junit.assertDefinitions

class BadInstanceCreationTest : AutoCloseKoinTest() {

    val module1 = applicationContext {
        bean { ComponentA() as MyInterface }
        bean { ComponentB() } bind MyInterface::class
    }

    val module2 = applicationContext {
        bean { ComponentA() } bind MyInterface::class
        bean { ComponentB() } bind MyInterface::class
    }

    val module3 = applicationContext {
        bean { ComponentC(get()) }
    }

    val module4 = applicationContext {
        bean { ComponentD(get()) }
        bean { ComponentE(get()) }
    }

    val module5 = applicationContext {
        bean { ComponentError() }
    }

    val module6 = applicationContext {
        bean { ComponentA() as MyInterface } bind MyInterface::class
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
        assertDefinedInScope(MyInterface::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
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
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
    }

//    @Test
//    fun `multiple beanDefinition for same type`() {
//        startKoin(listOf(module1))
//        try {
//            dryRun()
//            fail()
//        } catch (e: Exception) {
//            System.err.println(e)
//        }
//    }
//
//    @Test
//    fun `multiple beanDefinition for same type - binds`() {
//        startKoin(listOf(module2))
//        try {
//            dryRun()
//            fail()
//        } catch (e: Exception) {
//            System.err.println(e)
//        }
//    }

    @Test
    fun `missing dependency`() {
        startKoin(listOf(module3))
        try {
            dryRun()
            fail()
        } catch (e: Exception) {
            System.err.println(e)
        }
    }

    @Test
    fun `cyclic dependency`() {
        startKoin(listOf(module4))
        try {
            dryRun()
            fail()
        } catch (e: Exception) {
            System.err.println(e)
        }
    }

    @Test
    fun `bean internal error`() {
        startKoin(listOf(module5))
        try {
            dryRun()
            fail()
        } catch (e: Exception) {
            System.err.println(e)
        }
    }

    @Test
    fun `multiple bean definitions`(){
        startKoin(listOf(module6))
        dryRun()
        Assert.assertNotNull(get<MyInterface>())
    }
}