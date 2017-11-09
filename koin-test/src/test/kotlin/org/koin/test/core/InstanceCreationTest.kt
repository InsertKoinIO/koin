//package org.koin.test.core
//
//import org.junit.Assert
//import org.junit.Test
//import org.koin.Koin
//import org.koin.core.scope.Scope
//import org.koin.dsl.module.Module
//import org.koin.test.ext.assertContexts
//import org.koin.test.ext.assertDefinedInScope
//import org.koin.test.ext.assertDefinitions
//import org.koin.test.ext.assertRemainingInstances
//
//
///**
// * Created by arnaud on 01/06/2017.
// */
//class InstanceCreationTest {
//
//    class FlatModule : Module() {
//        override fun context() =
//                applicationContext {
//                    provide { ComponentA() }
//                    provide { ComponentB(get()) }
//                    provide { ComponentC(get(), get()) }
//                }
//    }
//
//    class HierarchicModule : Module() {
//        override fun context() =
//                applicationContext {
//                    provide { ComponentA() }
//
//                    context("B") {
//                        provide { ComponentB(get()) }
//
//                        context("C") {
//                            provide { ComponentC(get(), get()) }
//                        }
//                    }
//                }
//    }
//
//    class ComponentA
//    class ComponentB(val componentA: ComponentA)
//    class ComponentC(val componentB: ComponentB, val componentA: ComponentA)
//
//    @Test
//    fun `load and create instances for flat module`() {
//        val ctx = Koin().build(listOf(FlatModule()))
//
//        val a = ctx.get<ComponentA>()
//        val b = ctx.get<ComponentB>()
//        val c = ctx.get<ComponentC>()
//
//        Assert.assertNotNull(a)
//        Assert.assertNotNull(b)
//        Assert.assertNotNull(c)
//        Assert.assertEquals(a, b.componentA)
//        Assert.assertEquals(a, c.componentA)
//        Assert.assertEquals(b, c.componentB)
//
//        ctx.assertRemainingInstances(3)
//        ctx.assertDefinitions(3)
//        ctx.assertContexts(1)
//        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)
//        ctx.assertDefinedInScope(ComponentB::class, Scope.ROOT)
//        ctx.assertDefinedInScope(ComponentC::class, Scope.ROOT)
//    }
//
//    @Test
//    fun `load and create instances for hierarchic context`() {
//        val ctx = Koin().build(listOf(HierarchicModule()))
//
//        val a = ctx.get<ComponentA>()
//        val b = ctx.get<ComponentB>()
//        val c = ctx.get<ComponentC>()
//
//        Assert.assertNotNull(a)
//        Assert.assertNotNull(b)
//        Assert.assertNotNull(c)
//        Assert.assertEquals(a, b.componentA)
//        Assert.assertEquals(a, c.componentA)
//        Assert.assertEquals(b, c.componentB)
//
//        ctx.assertRemainingInstances(3)
//        ctx.assertDefinitions(3)
//        ctx.assertContexts(3)
//        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)
//        ctx.assertDefinedInScope(ComponentB::class, "B")
//        ctx.assertDefinedInScope(ComponentC::class, "C")
//    }
//
//}