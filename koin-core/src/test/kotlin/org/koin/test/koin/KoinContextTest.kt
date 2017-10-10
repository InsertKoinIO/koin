//package org.koin.test.koin
//
//import org.junit.Assert.*
//import org.junit.Test
//import org.koin.Koin
//import org.koin.error.NoBeanDefFoundException
//import org.koin.test.ext.assertContexts
//import org.koin.test.ext.assertSizes
//import org.koin.test.ext.getOrNull
//import org.koin.test.koin.example.ServiceA
//import org.koin.test.koin.example.ServiceB
//import org.koin.test.koin.example.ServiceOne
//import org.koin.test.koin.example.ServiceTwo
//import org.mockito.Mockito
//import org.mockito.Mockito.times
//
///**
// * Created by arnaud on 31/05/2017.
// */
//class KoinContextTest {
//
//    @Test
//    fun `no module - provide components`() {
//        val ctx = Koin().build()
//
//        ctx.provide { ServiceB() }
//        ctx.provide { ServiceA(ctx.get()) }
//
//        ctx.assertSizes(2, 0)
//
//        assertNotNull(ctx.get<ServiceA>())
//        assertNotNull(ctx.get<ServiceB>())
//
//        ctx.assertSizes(2, 2)
//        ctx.assertContexts(1)
//    }
//
//    @Test
//    fun `circular deps`() {
//        val ctx = Koin().build()
//
//        ctx.provide { ServiceTwo(ctx.get()) }
//        ctx.provide { ServiceOne(ctx.get()) }
//
//        ctx.assertSizes(2, 0)
//
//        assertNull(ctx.getOrNull<ServiceTwo>())
//        assertNull(ctx.getOrNull<ServiceOne>())
//
//        ctx.assertSizes(2, 0)
//        ctx.assertContexts(1)
//    }
//
//    @Test
//    fun `functional provide a mock`() {
//        val ctx = Koin().build()
//
//        val serviceB: ServiceB = Mockito.mock(ServiceB::class.java)
//        Mockito.`when`(serviceB.process()).then {
//            println("done B Mock")
//        }
//
//        ctx.provide { serviceB }
//        ctx.provide({ ServiceA(ctx.get()) })
//
//        ctx.assertSizes(2, 0)
//
//        val serviceA_1 = ctx.get<ServiceA>()
//        serviceA_1.doSomethingWithB()
//
//        val serviceA_2 = ctx.get<ServiceA>()
//        serviceA_2.doSomethingWithB()
//
//        ctx.assertSizes(2, 2)
//
//        assertEquals(serviceA_1, serviceA_2)
//        Mockito.verify(serviceB, times(2)).process()
//    }
//
//    @Test
//    fun `safe missing bean`() {
//        val ctx = Koin().build()
//
//        ctx.provide { ServiceB() }
//
//        assertNull(ctx.getOrNull<ServiceA>())
//        ctx.assertSizes(1, 0)
//    }
//
//    @Test
//    fun `unsafe missing bean`() {
//        val ctx = Koin().build()
//
//        try {
//            assertNull(ctx.get<ServiceA>())
//        } catch (e: NoBeanDefFoundException) {
//            assertNotNull(e)
//        }
//
//        ctx.assertSizes(0, 0)
//    }
//
//}