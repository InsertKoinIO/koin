package org.koin.test.core

import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.error.BeanOverrideException
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertDefinitions

class OverrideTest : AutoCloseKoinTest() {

    val diffBind = module {
        single { ComponentA() } bind MyInterface::class
        single { ComponentA() }
    }

    val diffName = module {
        single("A") { ComponentA() as MyInterface }
        single("B") { ComponentB() as MyInterface }
    }

    val sameType = module {
        single { ComponentA() }
        single { ComponentA() }
    }

    val overrideSameType = module {
        single { ComponentA() }
        single(override = true) { ComponentA() }
    }

    val diffKind = module {
        single { ComponentB() as MyInterface }
        factory { ComponentB() as MyInterface }
    }

    val diffPath = module {
        single { ComponentA() }

        module("aModule") {
            single { ComponentA() }
        }
    }

    val moduleA = module("org.koin.sample") {
        single { ComponentA() as MyInterface }
    }

    val moduleB = module("org.koin.sample", override = true) {
        single { ComponentB() as MyInterface }
    }

    class ComponentA : MyInterface
    class ComponentB : MyInterface
    interface MyInterface

    @Test
    fun `module override`() {
        startKoin(listOf(moduleA, moduleB))
        assertDefinitions(1)

        val b = get<MyInterface>()
        Assert.assertTrue(b is ComponentB)
    }

    @Test
    fun `can't override same type`() {
        try {
            startKoin(listOf(sameType))
            fail()
        } catch (e: BeanOverrideException) {
            e.printStackTrace()
        }

        assertDefinitions(0)
    }

    @Test
    fun `override same type`() {
        startKoin(listOf(overrideSameType))

        assertDefinitions(1)
    }

    @Test
    fun `no override - bind `() {
        startKoin(listOf(diffBind))

        assertDefinitions(2)
    }

    @Test
    fun `no override - name `() {
        startKoin(listOf(diffName))

        assertDefinitions(2)
    }

    @Test
    fun `no override - Kind `() {
        startKoin(listOf(diffKind))

        assertDefinitions(2)
    }

    @Test
    fun `no override - Module path `() {
        startKoin(listOf(diffPath))

        assertDefinitions(2)
    }
}