//package org.koin.core
//
//import org.junit.Assert
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertNotEquals
//import org.junit.Test
//import org.koin.Simple
//import org.koin.core.scope.Scope
//import org.koin.dsl.SCOPE_ID
//import org.koin.dsl.koinApplication
//import org.koin.dsl.module
//import org.koin.test.getDefinition
//
//class DetachedScopeTest {
//    @Test
//    fun `detach a scope - should be different each time`() {
//        val app = koinApplication {
//            modules(module {
//                scope(SCOPE_ID) { Simple.ComponentA() }
//            })
//        }
//        val koin = app.koin
//
//        val scope1: Scope = koin.detachScope(SCOPE_ID)
//        val scope2: Scope = koin.detachScope(SCOPE_ID)
//        assertNotEquals(scope1, scope2)
//    }
//
//    @Test
//    fun `can retrieve a detached a scope`() {
//        val app = koinApplication {
//            modules(module {
//                scope(SCOPE_ID) { Simple.ComponentA() }
//            })
//        }
//        val koin = app.koin
//
//        val scope1: Scope = koin.detachScope(SCOPE_ID)
//        val scope2: Scope = koin.getDetachedScope(scope1.uuid) ?: error("no detached scope found")
//
//        assertEquals(scope1, scope2)
//    }
//
//    @Test
//    fun `can resolve components from a detached a scope`() {
//        val app = koinApplication {
//            modules(module {
//                scope(SCOPE_ID) { Simple.ComponentA() }
//            })
//        }
//        val koin = app.koin
//
//        val scope1: Scope = koin.detachScope(SCOPE_ID)
//        val scope2: Scope = koin.detachScope(SCOPE_ID)
//
//        val as1 = koin.get<Simple.ComponentA>(scope = scope1)
//        val as2 = koin.get<Simple.ComponentA>(scope = scope2)
//
//        assertNotEquals(as1, as2)
//    }
//
//    @Test
//    fun `closing scope don't impact other detached scopes`() {
//        val app = koinApplication {
//            modules(module {
//                scope(SCOPE_ID) { Simple.ComponentA() }
//            })
//        }
//        val koin = app.koin
//
//        val scope1: Scope = koin.detachScope(SCOPE_ID)
//        val scope2: Scope = koin.detachScope(SCOPE_ID)
//
//        val as1 = koin.get<Simple.ComponentA>(scope = scope1)
//        val as2 = koin.get<Simple.ComponentA>(scope = scope2)
//
//        scope1.close()
//
//        assertNotEquals(as1, as2)
//        assertEquals(as2, koin.get<Simple.ComponentA>(scope = scope2))
//    }
//
//    @Test
//    fun `can resolve components from a detached and default scope`() {
//        val app = koinApplication {
//            modules(module {
//                scope(SCOPE_ID) { Simple.ComponentA() }
//                scope(SCOPE_ID) { Simple.ComponentB(get()) }
//            })
//        }
//        val koin = app.koin
//
//        val scope1: Scope = koin.createScope(SCOPE_ID)
//        val scope2: Scope = koin.detachScope(SCOPE_ID)
//
//        val as1 = koin.get<Simple.ComponentA>()
//        val as2 = koin.get<Simple.ComponentA>(scope = scope2)
//
//        assertNotEquals(as1, as2)
//
//        val defA = app.getDefinition(Simple.ComponentA::class)!!
//        Assert.assertTrue(defA.instance.isCreated(scope1))
//
//        val defB = app.getDefinition(Simple.ComponentB::class)!!
//        Assert.assertTrue(!defB.instance.isCreated(scope2))
//    }
//}