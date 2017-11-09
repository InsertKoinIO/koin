//package org.koin.test.core
//
//import org.junit.Test
//import org.koin.Koin
//import org.koin.core.scope.Scope
//import org.koin.dsl.module.Module
//import org.koin.test.ext.assertContexts
//import org.koin.test.ext.assertDefinedInScope
//import org.koin.test.ext.assertDefinitions
//import org.koin.test.ext.assertScopeParent
//
//// getScopeForDefinition qualifier
//
//
//class DSLTest {
//
//    class FlatContextsModule() : Module() {
//        override fun context() = applicationContext {
//
//            provide { ComponentA() }
//
//            context(name = "B") {
//                provide { ComponentB() }
//            }
//
//            context(name = "C") {
//                provide { ComponentC() }
//            }
//        }
//    }
//
//    class HierarchyContextsModule() : Module() {
//        override fun context() = applicationContext {
//            context(name = "A") {
//                provide { ComponentA() }
//
//                context(name = "B") {
//                    provide { ComponentB() }
//
//                    context(name = "C") {
//                        provide { ComponentC() }
//                    }
//                }
//            }
//        }
//    }
//
//    class ComponentA
//    class ComponentB
//    class ComponentC
//
//
//    @Test
//    fun `can create flat contexts`() {
//        val ctx = Koin().build(listOf(FlatContextsModule()))
//
//        ctx.assertContexts(3)
//        ctx.assertDefinitions(3)
//
//        ctx.assertDefinedInScope(ComponentA::class, Scope.ROOT)
//        ctx.assertDefinedInScope(ComponentB::class, "B")
//        ctx.assertDefinedInScope(ComponentC::class, "C")
//
//        ctx.assertScopeParent("B", Scope.ROOT)
//        ctx.assertScopeParent("C", Scope.ROOT)
//    }
//
//    @Test
//    fun `can create hierarchic contexts`() {
//        val ctx = Koin().build(listOf(HierarchyContextsModule()))
//
//        ctx.assertContexts(4)
//        ctx.assertDefinitions(3)
//
//        ctx.assertDefinedInScope(ComponentA::class, "A")
//        ctx.assertDefinedInScope(ComponentB::class, "B")
//        ctx.assertDefinedInScope(ComponentC::class, "C")
//
//        ctx.assertScopeParent("A", Scope.ROOT)
//        ctx.assertScopeParent("B", "A")
//        ctx.assertScopeParent("C", "B")
//    }
//
//}