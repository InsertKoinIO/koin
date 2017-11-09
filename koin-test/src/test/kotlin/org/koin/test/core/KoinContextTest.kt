//package org.koin.test.core
//
//import org.junit.Assert.assertNull
//import org.junit.Assert.fail
//import org.junit.Test
//import org.koin.Koin
//import org.koin.dsl.module.Module
//import org.koin.error.BeanInstanceCreationException
//import org.koin.test.ext.assertContexts
//import org.koin.test.ext.assertDefinitions
//import org.koin.test.ext.assertRemainingInstances
//import org.koin.test.ext.getOrNull
//
///**
// * Created by arnaud on 31/05/2017.
// */
//class KoinContextTest {
//
//    class CircularDeps() : Module() {
//        override fun context() = applicationContext {
//            provide { ComponentA(get()) }
//            provide { ComponentB(get()) }
//        }
//    }
//
//    class SingleModule() : Module() {
//        override fun context() = applicationContext {
//            provide { ComponentA(get()) }
//        }
//    }
//
//    class ComponentA(val componentB: ComponentB)
//    class ComponentB(val componentA: ComponentA)
//
//    @Test
//    fun `circular deps injection error`() {
//        val ctx = Koin().build(listOf(CircularDeps()))
//
//        ctx.assertDefinitions(2)
//        ctx.assertRemainingInstances(0)
//
//        assertNull(ctx.getOrNull<ComponentA>())
//        assertNull(ctx.getOrNull<ComponentB>())
//
//        ctx.assertRemainingInstances(0)
//        ctx.assertContexts(1)
//    }
//
//    @Test
//    fun `safe missing bean`() {
//        val ctx = Koin().build(listOf(SingleModule()))
//
//        ctx.assertDefinitions(1)
//        ctx.assertRemainingInstances(0)
//        assertNull(ctx.getOrNull<ComponentA>())
//        ctx.assertRemainingInstances(0)
//    }
//
//    @Test
//    fun `unsafe missing bean`() {
//        val ctx = Koin().build(listOf(SingleModule()))
//
//        ctx.assertDefinitions(1)
//        ctx.assertRemainingInstances(0)
//        try {
//            assertNull(ctx.get<ComponentA>())
//            fail("should not inject ")
//        } catch (e: BeanInstanceCreationException) {
//            System.err.println(e)
//        }
//        ctx.assertRemainingInstances(0)
//    }
//
//}