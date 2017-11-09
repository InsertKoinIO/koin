//package org.koin.test.core
//
//import junit.framework.Assert.fail
//import org.junit.Assert
//import org.junit.Test
//import org.koin.Koin
//import org.koin.core.scope.Scope
//import org.koin.dsl.module.Module
//import org.koin.error.MissingPropertyException
//import org.koin.test.ext.*
//
//class PropertyTest {
//    class SimpleModule() : Module() {
//        override fun context() = applicationContext {
//
//            property(K_URL to K_URL_VAL)
//
//            provide { ComponentA(getProperty(K_URL)) }
//            provide { ComponentB(get()) }
//        }
//    }
//
//    class NoPropertyModule() : Module() {
//        override fun context() = applicationContext {
//
//            provide { ComponentA(getProperty(K_URL)) }
//            provide { ComponentB(get()) }
//        }
//    }
//
//    class ComplexModule() : Module() {
//        override fun context() = applicationContext {
//            provide { ComponentB(get()) }
//            context("A") {
//                provide { ComponentA(getProperty(K_URL)) }
//            }
//        }
//    }
//
//    class MoreComplexModule() : Module() {
//        override fun context() = applicationContext {
//            provide { ComponentB(get()) }
//            context("A") {
//                provideFactory { ComponentA(getProperty(K_URL)) }
//            }
//        }
//    }
//
//    class ComponentA(val url: String)
//    class ComponentB(val componentA: ComponentA)
//
//    @Test
//    fun `should inject property`() {
//        val ctx = Koin().build(listOf(SimpleModule()))
//        ctx.setProperty(K_URL, K_URL_VAL)
//
//        val url = ctx.getProperty<String>(K_URL)
//        val a = ctx.get<ComponentA>()
//        val b = ctx.get<ComponentB>()
//
//        Assert.assertEquals(a, b.componentA)
//        Assert.assertEquals(K_URL_VAL, a.url)
//        Assert.assertEquals(url, a.url)
//
//        ctx.assertRemainingInstances(2)
//        ctx.assertDefinitions(2)
//        ctx.assertContexts(1)
//        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)
//        ctx.assertDefinedInScope(ComponentB::class, Scope.ROOT)
//        ctx.assertProperties(1)
//    }
//
//
//    @Test
//    fun `should inject property - at build`() {
//        val ctx = Koin().build(listOf(SimpleModule()))
//
//        val url = ctx.getProperty<String>(K_URL)
//        val a = ctx.get<ComponentA>()
//        val b = ctx.get<ComponentB>()
//
//        Assert.assertEquals(a, b.componentA)
//        Assert.assertEquals(K_URL_VAL, a.url)
//        Assert.assertEquals(url, a.url)
//
//        ctx.assertRemainingInstances(2)
//        ctx.assertDefinitions(2)
//        ctx.assertContexts(1)
//        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)
//        ctx.assertDefinedInScope(ComponentB::class, Scope.ROOT)
//        ctx.assertProperties(1)
//    }
//
//    @Test
//    fun `should inject property - complex module`() {
//        val ctx = Koin().build(listOf(ComplexModule()))
//        ctx.setProperty(K_URL, K_URL_VAL)
//
//        val url = ctx.getProperty<String>(K_URL)
//        val a = ctx.get<ComponentA>()
//        val b = ctx.get<ComponentB>()
//
//        Assert.assertEquals(a, b.componentA)
//        Assert.assertEquals(K_URL_VAL, a.url)
//        Assert.assertEquals(url, a.url)
//
//        ctx.assertRemainingInstances(2)
//        ctx.assertDefinitions(2)
//        ctx.assertContexts(2)
//        ctx.assertDefinedInScope(ComponentA::class, "A")
//        ctx.assertDefinedInScope(ComponentB::class, Scope.ROOT)
//        ctx.assertProperties(1)
//    }
//
//    @Test
//    fun `should not inject property but get default value as return`() {
//        val ctx = Koin().build(listOf(NoPropertyModule()))
//
//        var url: String? = null
//        try {
//            url = ctx.getProperty<String>(K_URL)
//            fail()
//        } catch (e: MissingPropertyException) {
//            System.err.println(e)
//        }
//        val urlWithDefault = ctx.getProperty(K_URL, "DEFAULT")
//
//        Assert.assertNull(url)
//        Assert.assertEquals(urlWithDefault, "DEFAULT")
//    }
//
//    @Test
//    fun `should not inject property`() {
//        val ctx = Koin().build(listOf(NoPropertyModule()))
//
//        var url: String? = null
//        try {
//            url = ctx.getProperty<String>(K_URL)
//            fail()
//        } catch (e: MissingPropertyException) {
//            System.err.println(e)
//        }
//        val a = ctx.getOrNull<ComponentA>()
//        val b = ctx.getOrNull<ComponentB>()
//
//        Assert.assertNull(url)
//        Assert.assertNull(a)
//        Assert.assertNull(b)
//
//        ctx.assertRemainingInstances(0)
//        ctx.assertDefinitions(2)
//        ctx.assertContexts(1)
//        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)
//        ctx.assertDefinedInScope(ComponentB::class, Scope.ROOT)
//        ctx.assertProperties(0)
//    }
//
//    @Test
//    fun `should overwrite property`() {
//        val ctx = Koin().build(listOf(MoreComplexModule()))
//        ctx.setProperty(K_URL, K_URL_VAL)
//
//        var url = ctx.getProperty<String>(K_URL)
//        var a = ctx.get<ComponentA>()
//        var b = ctx.get<ComponentB>()
//
//        Assert.assertEquals(K_URL_VAL, a.url)
//        Assert.assertEquals(url, a.url)
//        Assert.assertEquals(b.componentA.url, a.url)
//
//        ctx.assertRemainingInstances(1)
//        ctx.assertDefinitions(2)
//
//        ctx.setProperty(K_URL, K_URL_VAL2)
//
//        url = ctx.getProperty<String>(K_URL)
//        a = ctx.get()
//
//        Assert.assertEquals(url, a.url)
//        Assert.assertEquals(K_URL_VAL2, a.url)
//
//        ctx.assertRemainingInstances(1)
//        ctx.assertDefinitions(2)
//
//        ctx.assertContexts(2)
//        ctx.assertProperties(1)
//        ctx.assertDefinedInScope(ComponentA::class, "A")
//        ctx.assertDefinedInScope(ComponentB::class, Scope.ROOT)
//    }
//
//    companion object {
//        val K_URL = "URL"
//        val K_URL_VAL = "http://..."
//        val K_URL_VAL2 = "http://...2"
//    }
//}