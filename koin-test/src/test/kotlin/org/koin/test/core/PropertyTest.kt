package org.koin.test.core

import junit.framework.Assert.fail
import org.junit.Assert
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.error.MissingPropertyException
import org.koin.standalone.bindProperty
import org.koin.standalone.startContext
import org.koin.test.KoinTest
import org.koin.test.ext.junit.*
import org.koin.test.get
import org.koin.test.getProperty

class PropertyTest : KoinTest {
    class SimpleModule() : Module() {
        override fun context() = applicationContext {

            property(K_URL to K_URL_VAL)

            provide { ComponentA(getProperty(K_URL)) }
            provide { ComponentB(get()) }
        }
    }

    class NoPropertyModule() : Module() {
        override fun context() = applicationContext {

            provide { ComponentA(getProperty(K_URL)) }
            provide { ComponentB(get()) }
        }
    }

    class ComplexModule() : Module() {
        override fun context() = applicationContext {
            provide { ComponentB(get()) }
            context("A") {
                provide { ComponentA(getProperty(K_URL)) }
            }
        }
    }

    class MoreComplexModule() : Module() {
        override fun context() = applicationContext {
            provide { ComponentB(get()) }
            context("A") {
                provideFactory { ComponentA(getProperty(K_URL)) }
            }
        }
    }

    class ComponentA(val url: String)
    class ComponentB(val componentA: ComponentA)

    @Test
    fun `should inject external property`() {
        startContext(listOf(SimpleModule()))
        bindProperty(K_URL, K_URL_VAL)

        val url = getProperty<String>(K_URL)
        val a = get<ComponentA>()
        val b = get<ComponentB>()

        Assert.assertEquals(a, b.componentA)
        Assert.assertEquals(K_URL_VAL, a.url)
        Assert.assertEquals(url, a.url)

        assertRemainingInstances(2)
        assertDefinitions(2)
        assertContexts(1)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
        assertProperties(1)
    }


    @Test
    fun `should inject internal property`() {
        startContext(listOf(SimpleModule()))

        val url = getProperty<String>(K_URL)
        val a = get<ComponentA>()
        val b = get<ComponentB>()

        Assert.assertEquals(a, b.componentA)
        Assert.assertEquals(K_URL_VAL, a.url)
        Assert.assertEquals(url, a.url)

        assertRemainingInstances(2)
        assertDefinitions(2)
        assertContexts(1)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
        assertProperties(1)
    }

    @Test
    fun `should inject property - complex module`() {
        startContext(listOf(ComplexModule()))
        bindProperty(K_URL, K_URL_VAL)

        val url = getProperty<String>(K_URL)
        val a = get<ComponentA>()
        val b = get<ComponentB>()

        Assert.assertEquals(a, b.componentA)
        Assert.assertEquals(K_URL_VAL, a.url)
        Assert.assertEquals(url, a.url)

        assertRemainingInstances(2)
        assertDefinitions(2)
        assertContexts(2)
        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
        assertProperties(1)
    }

    @Test
    fun `should not inject property but get default value as return`() {
        startContext(listOf(NoPropertyModule()))

        try {
            getProperty<String>(K_URL)
            fail()
        } catch (e: MissingPropertyException) {
            System.err.println(e)
        }
        val urlWithDefault = getProperty(K_URL, "DEFAULT")

        Assert.assertEquals(urlWithDefault, "DEFAULT")
    }

    @Test
    fun `should not inject property`() {
        startContext(listOf(NoPropertyModule()))

        try {
            getProperty<String>(K_URL)
            fail()
        } catch (e: MissingPropertyException) {
            System.err.println(e)
        }
        try {
            get<ComponentA>()
            fail()
        } catch (e: Exception) {
        }
        try {
            get<ComponentB>()
            fail()
        } catch (e: Exception) {
        }

        assertRemainingInstances(0)
        assertDefinitions(2)
        assertContexts(1)
        assertDefinedInScope(ComponentA::class, Scope.ROOT)
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
        assertProperties(0)
    }

    @Test
    fun `should overwrite property`() {
        startContext(listOf(MoreComplexModule()))
        bindProperty(K_URL, K_URL_VAL)

        var url = getProperty<String>(K_URL)
        var a = get<ComponentA>()
        val b = get<ComponentB>()

        Assert.assertEquals(K_URL_VAL, a.url)
        Assert.assertEquals(url, a.url)
        Assert.assertEquals(b.componentA.url, a.url)

        assertRemainingInstances(1)
        assertDefinitions(2)

        bindProperty(K_URL, K_URL_VAL2)

        url = getProperty<String>(K_URL)
        a = get()

        Assert.assertEquals(url, a.url)
        Assert.assertEquals(K_URL_VAL2, a.url)

        assertRemainingInstances(1)
        assertDefinitions(2)

        assertContexts(2)
        assertProperties(1)
        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, Scope.ROOT)
    }

    companion object {
        val K_URL = "URL"
        val K_URL_VAL = "http://..."
        val K_URL_VAL2 = "http://...2"
    }
}