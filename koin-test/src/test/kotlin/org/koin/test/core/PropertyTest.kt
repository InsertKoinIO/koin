package org.koin.test.core

import junit.framework.Assert.fail
import org.junit.Assert
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.error.MissingPropertyException
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.setProperty
import org.koin.test.AbstractKoinTest
import org.koin.test.ext.junit.*
import org.koin.test.get
import org.koin.test.getProperty

class PropertyTest : AbstractKoinTest() {
    class SimpleModule() : Module() {
        override fun context() = applicationContext {

            provide { ComponentA(getProperty(KEY)) }
            provide { ComponentB(get()) }
        }
    }

    class NoPropertyModule() : Module() {
        override fun context() = applicationContext {

            provide { ComponentA(getProperty(KEY)) }
            provide { ComponentB(get()) }
        }
    }

    class ComplexModule() : Module() {
        override fun context() = applicationContext {
            context("A") {
                provide { ComponentB(get()) }
                provide { ComponentA(getProperty(KEY)) }
            }
        }
    }

    class MoreComplexModule() : Module() {
        override fun context() = applicationContext {
            context("A") {
                provide { ComponentB(get()) }
                provideFactory { ComponentA(getProperty(KEY)) }
            }
        }
    }

    class ComponentA(val url: String)
    class ComponentB(val componentA: ComponentA)

    @Test
    fun `should inject external property`() {
        startKoin(listOf(SimpleModule()))
        setProperty(KEY, VALUE)

        val url = getProperty<String>(KEY)
        val a = get<ComponentA>()
        val b = get<ComponentB>()

        Assert.assertEquals(a, b.componentA)
        Assert.assertEquals(VALUE, a.url)
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
        startKoin(listOf(SimpleModule()), properties = mapOf(KEY to VALUE))

        val url = getProperty<String>(KEY)
        val a = get<ComponentA>()
        val b = get<ComponentB>()

        Assert.assertEquals(a, b.componentA)
        Assert.assertEquals(VALUE, a.url)
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
        startKoin(listOf(ComplexModule()))
        setProperty(KEY, VALUE)

        val url = getProperty<String>(KEY)
        val a = get<ComponentA>()
        val b = get<ComponentB>()

        Assert.assertEquals(a, b.componentA)
        Assert.assertEquals(VALUE, a.url)
        Assert.assertEquals(url, a.url)

        assertRemainingInstances(2)
        assertDefinitions(2)
        assertContexts(2)
        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "A")
        assertProperties(1)
    }

    @Test
    fun `should not inject property but get default value as return`() {
        startKoin(listOf(NoPropertyModule()))

        try {
            getProperty<String>(KEY)
            fail()
        } catch (e: MissingPropertyException) {
            System.err.println(e)
        }
        val urlWithDefault = getProperty(KEY, "DEFAULT")

        Assert.assertEquals(urlWithDefault, "DEFAULT")
    }

    @Test
    fun `should not inject property`() {
        startKoin(listOf(NoPropertyModule()))

        try {
            getProperty<String>(KEY)
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
        startKoin(listOf(MoreComplexModule()))
        setProperty(KEY, VALUE)

        var url = getProperty<String>(KEY)
        var a = get<ComponentA>()
        val b = get<ComponentB>()

        Assert.assertEquals(VALUE, a.url)
        Assert.assertEquals(url, a.url)
        Assert.assertEquals(b.componentA.url, a.url)

        assertRemainingInstances(1)
        assertDefinitions(2)

        setProperty(KEY, VALUE_2)

        url = getProperty<String>(KEY)
        a = get()

        Assert.assertEquals(url, a.url)
        Assert.assertEquals(VALUE_2, a.url)

        assertRemainingInstances(1)
        assertDefinitions(2)

        assertContexts(2)
        assertProperties(1)
        assertDefinedInScope(ComponentA::class, "A")
        assertDefinedInScope(ComponentB::class, "A")
    }

    companion object {
        val KEY = "URL"
        val VALUE = "http://..."
        val VALUE_2 = "http://...2"
    }
}