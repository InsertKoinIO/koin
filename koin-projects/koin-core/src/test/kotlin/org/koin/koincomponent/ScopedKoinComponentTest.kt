// package org.koin.koincomponent
//
// import org.junit.After
// import org.junit.Assert.assertEquals
// import org.junit.Assert.assertNotEquals
// import org.junit.Test
// import org.koin.Simple
// import org.koin.core.KoinComponent
// import org.koin.core.context.GlobalContext
// import org.koin.core.context.startKoin
// import org.koin.core.context.stopKoin
// import org.koin.core.inject
// import org.koin.core.logger.Level
// import org.koin.core.qualifier.named
// import org.koin.dsl.module
//
// class ScopedKoinComponentTest {
//
//    @After
//    fun after() {
//        stopKoin()
//    }
//
//    @Test
//    fun `Scoped component - different scopes`() {
//        startKoin {
//            printLogger(Level.DEBUG)
//            modules(
//                    module {
//                        single { Simple.ComponentA() }
//
//                        scope(SCOPE_DEF_NAME) {
//                            scoped { Simple.ComponentB(get()) }
//                        }
//                    }
//            )
//        }
//
//        val koin = GlobalContext.get().koin
//
//        val scopeId1 = "scope_id_1"
//        val scope1 = koin.createScope(scopeId1, SCOPE_DEF_NAME)
//        val scopedComponent1 = ScopedComponent(scopeId1)
//
//        val scopeId2 = "scope_id_2"
//        val scope2 = koin.createScope(scopeId2, SCOPE_DEF_NAME)
//        val scopedComponent2 = ScopedComponent(scopeId2)
//
//        assertEquals(scopedComponent1.a, scopedComponent2.a)
//        assertNotEquals(scopedComponent1.b, scopedComponent2.b)
//
//        assertEquals(scopedComponent1.currentScope(), scope1)
//        assertEquals(scopedComponent2.currentScope(), scope2)
//    }
//
//    @Test
//    fun `Scoped component - same scopes`() {
//        startKoin {
//            printLogger(Level.DEBUG)
//            modules(
//                    module {
//                        single { Simple.ComponentA() }
//
//                        scope(SCOPE_DEF_NAME) {
//                            scoped { Simple.ComponentB(get()) }
//                        }
//                    }
//            )
//        }
//
//        val koin = GlobalContext.get().koin
//
//        val scopeId1 = "scope_id_1"
//        val scope1 = koin.createScope(scopeId1, SCOPE_DEF_NAME)
//        val scopedComponent1 = ScopedComponent(scopeId1)
//        val scopedComponent2 = ScopedComponent(scopeId1)
//
//        assertEquals(scopedComponent1.a, scopedComponent2.a)
//        assertEquals(scopedComponent1.b, scopedComponent2.b)
//
//        assertEquals(scopedComponent1.currentScope(), scope1)
//        assertEquals(scopedComponent2.currentScope(), scope1)
//    }
//
// }
//
// val SCOPE_DEF_NAME = named("MY_SCOPE")
//
// class ScopedComponent(val scopeId: String) : KoinComponent {
//    override fun currentScope() = getKoin().getScope(scopeId)
//
//    val a: Simple.ComponentA by inject()
//    val b: Simple.ComponentB by inject()
// }