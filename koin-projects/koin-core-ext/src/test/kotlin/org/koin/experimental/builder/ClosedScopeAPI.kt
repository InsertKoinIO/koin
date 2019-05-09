// package org.koin.experimental.builder
//
// import org.junit.Assert
// import org.junit.Test
// import org.koin.dsl.koinApplication
// import org.koin.dsl.module
//
// class ClosedScopeAPI {
//
//    val scopeName = "MY_SCOPE"
//
//    @Test
//    fun `get definition from open scope`() {
//        val koin = koinApplication {
//            modules(
//                module {
//                    scoped<ComponentA>()
//                    scoped<ComponentB>()
//                }
//            )
//        }.koin
//
//        val scope = koin.createScope("myScope")
//        Assert.assertEquals(scope.get<ComponentB>(), scope.get<ComponentB>())
//        Assert.assertEquals(scope.get<ComponentA>(), scope.get<ComponentB>().a)
//    }
//
//    @Test
//    fun `get definition from closed scope`() {
//        val koin = koinApplication {
//            modules(
//                module {
//                    scope(scopeName) {
//                        scoped<ComponentA>()
//                        scoped<ComponentB>()
//                    }
//                }
//            )
//        }.koin
//
//        val scope = koin.createScope("myScope", scopeName)
//        Assert.assertEquals(scope.get<ComponentB>(), scope.get<ComponentB>())
//        Assert.assertEquals(scope.get<ComponentA>(), scope.get<ComponentB>().a)
//    }
// }