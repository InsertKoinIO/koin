//package org.koin.dsl
//
//import org.junit.Assert.assertEquals
//import org.junit.Assert.fail
//import org.junit.Test
//import org.koin.Simple
//import org.koin.core.error.DefinitionOverrideException
//import org.koin.core.scope.getScopeName
//import org.koin.test.getDefinition
//
//const val SCOPE_ID = "myScope"
//
//class ScopeDeclarationTest {
//
//    @Test
//    fun `declare scoped definition`() {
//        val app = koinApplication {
//            modules(module {
//                scope(SCOPE_ID) { Simple.ComponentA() }
//            })
//        }
//
//        val defA = app.getDefinition(Simple.ComponentA::class)!!
//        assertEquals(SCOPE_ID, defA.getScopeName())
//    }
//
//    @Test
//    fun `conflicting scope definition - same type`() {
//        try {
//            koinApplication {
//                modules(module {
//                    single { Simple.ComponentA() }
//                    scope(SCOPE_ID) { Simple.ComponentA() }
//                })
//            }
//            fail("should not allow scope/single for same type")
//        } catch (e: DefinitionOverrideException) {
//            e.printStackTrace()
//        }
//    }
//
//    @Test
//    fun `non conflicting scope definition - different names`() {
//        try {
//            koinApplication {
//                modules(module {
//                    scope(SCOPE_ID) { Simple.ComponentA() }
//                    scope(SCOPE_ID, name = "default") { Simple.ComponentA() }
//                })
//            }
//        } catch (e: DefinitionOverrideException) {
//            e.printStackTrace()
//        }
//    }
//
//    @Test
//    fun `declare scope group declaration`() {
//        val app = koinApplication {
//            modules(module {
//                withScope(SCOPE_ID) {
//                    scoped { Simple.ComponentA() }
//                    scoped { Simple.ComponentB(get()) }
//                }
//            })
//        }
//
//        val defA = app.getDefinition(Simple.ComponentA::class)!!
//        assertEquals(SCOPE_ID, defA.getScopeName())
//        val defB = app.getDefinition(Simple.ComponentB::class)!!
//        assertEquals(SCOPE_ID, defB.getScopeName())
//    }
//}