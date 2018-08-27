//package org.koin.test.core
//
//import org.junit.Assert.assertEquals
//import org.junit.Test
//import org.koin.dsl.module.module
//import org.koin.log.PrintLogger
//import org.koin.standalone.StandAloneContext.startKoin
//import org.koin.standalone.get
//import org.koin.standalone.release
//import org.koin.test.AutoCloseKoinTest
//
//class SharedInstanceTest : AutoCloseKoinTest() {
//
//    class A
//    class B(val a: A)
//    class C(val a: A)
//
//    @Test
//    fun `has shared instance A`() {
//        startKoin(listOf(module {
//            shared { A() }
//            factory { B(get()) }
//            factory { C(get()) }
//        }), logger = PrintLogger(showDebug = true))
//
//        val b_a1 = get<B>()
//        assertEquals(b_a1.a, get<C>().a)
//
//        release("")
//
//        val b_a2 = get<B>()
//        assertEquals(b_a2.a, get<C>().a)
//    }
//
//    @Test
//    fun `has shared instance A - gc`() {
//        startKoin(listOf(module {
//            shared { A() }
//            factory { B(get()) }
//            factory { C(get()) }
//        }), logger = PrintLogger(showDebug = true))
//
//        val b_a1 = get<B>()
//        assertEquals(b_a1.a, get<C>().a)
//
//        System.gc()
//
//        val b_a2 = get<B>()
//        assertEquals(b_a2.a, get<C>().a)
//    }
//
//    @Test
//    fun `has shared instance A - diff module`() {
//        startKoin(listOf(module("mod1") {
//            shared { A() }
//            factory { B(get()) }
//            factory { C(get()) }
//        }))
//
////        val a1 = get<A>()
//        assertEquals(get<B>().a, get<C>().a)
//
//        release("mod1")
//
//        assertEquals(get<B>().a, get<C>().a)
////        val a2 = get<A>()
//
////        assertNotEquals(a1, a2)
//    }
//
//    @Test
//    fun `has shared instance A - release B`() {
//        startKoin(listOf(module {
//            shared { A() }
//            module("B") {
//                single { B(get()) }
//            }
//            module("C") {
//                single { C(get()) }
//            }
//        }))
//
//        assertEquals(get<B>().a, get<C>().a)
//
////        release("B")
////
//        assertEquals(get<B>().a, get<C>().a)
//    }
//
//    @Test
//    fun `has shared instance A - factory`() {
//        startKoin(listOf(module {
//            shared { A() }
//            module("B") {
//                factory { B(get()) }
//            }
//            module("C") {
//                factory  { C(get()) }
//            }
//        }))
//
//        assertEquals(get<B>().a, get<C>().a)
//        assertEquals(get<B>().a, get<C>().a)
//    }
//
//    @Test
//    fun `has shared instance A - module`() {
//        startKoin(listOf(module {
//            shared { A() }
//            module("mod") {
//                single { B(get()) }
//                single { C(get()) }
//            }
//        }), logger = PrintLogger(showDebug = true))
//
//        val a1 = get<B>().a
//        assertEquals(a1, get<C>().a)
//
//        release("mod")
//
//        val a2 = get<B>().a
//        assertEquals(a2, get<C>().a)
//    }
//
//}