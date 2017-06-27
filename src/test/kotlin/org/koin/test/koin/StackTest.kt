//package org.koin.test.koin
//
//import org.junit.Assert.*
//import org.junit.Test
//import org.koin.Koin
//import org.koin.test.koin.example.ServiceA
//import org.koin.test.koin.example.SampleModuleB
//import org.koin.test.koin.example.ServiceB
//
//
///**
// * Created by arnaud on 31/05/2017.
// */
//class StackTest {
//
//    @Test
//    fun `stack a definition and retrieve it`() {
//        val ctx = Koin().build()
//        ctx.stack { ServiceB() }
//
//        assertEquals(1, ctx.beanRegistry.definitions.size)
//        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
//
//        assertNotNull(ctx.getOrNull<ServiceB>())
//
//        assertEquals(0, ctx.beanRegistry.definitions.size)
//        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
//
//        assertNull(ctx.getOrNull<ServiceB>())
//    }
//
//    @Test
//    fun `stack same definitions`() {
//        val ctx = Koin().build(SampleModuleB::class)
//        ctx.stack { ServiceA(ctx.get()) }
//
//        assertEquals(2, ctx.beanRegistry.definitions.size)
//        assertEquals(0, ctx.beanRegistry.instanceFactory.instances.size)
//
//        assertNotNull(ctx.getOrNull<ServiceA>())
//
//        assertEquals(1, ctx.beanRegistry.definitions.size)
//        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)
//
//        ctx.stack { ServiceA(ctx.get()) }
//        assertNotNull(ctx.getOrNull<ServiceA>())
//
//        assertEquals(1, ctx.beanRegistry.definitions.size)
//        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)
//
//        assertNull(ctx.getOrNull<ServiceA>())
//
//        assertEquals(1, ctx.beanRegistry.definitions.size)
//        assertEquals(1, ctx.beanRegistry.instanceFactory.instances.size)
//    }
//}