//package org.koin.test.core
//
//import org.junit.Assert
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
//class DryRunTest {
//
//    class SimpleModule() : Module() {
//        override fun context() = applicationContext {
//            provide { ComponentA() }
//            provide { ComponentB(get()) }
//        }
//    }
//
//    class BrokenModule() : Module() {
//        override fun context() = applicationContext {
//            provide { ComponentB(get()) }
//        }
//    }
//
//    class ComponentA()
//    class ComponentB(val componentA: ComponentA)
//
//    @Test
//    fun `successful dry run`() {
//        val ctx = Koin().build(listOf(SimpleModule()))
//        ctx.dryRun()
//
//        ctx.assertDefinitions(2)
//        ctx.assertRemainingInstances(2)
//
//        Assert.assertNotNull(ctx.get<ComponentA>())
//        Assert.assertNotNull(ctx.get<ComponentB>())
//
//        ctx.assertRemainingInstances(2)
//        ctx.assertContexts(1)
//    }
//
//    @Test
//    fun `unsuccessful dry run`() {
//        val ctx = Koin().build(listOf(BrokenModule()))
//        try {
//            ctx.dryRun()
//            fail()
//        } catch (e: BeanInstanceCreationException) {
//            System.err.println(e)
//        }
//
//        ctx.assertDefinitions(1)
//        ctx.assertRemainingInstances(0)
//
//        Assert.assertNull(ctx.getOrNull<ComponentA>())
//        Assert.assertNull(ctx.getOrNull<ComponentB>())
//
//        ctx.assertRemainingInstances(0)
//        ctx.assertContexts(1)
//    }
//}