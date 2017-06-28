//package org.koin.test.koin
//
//import org.junit.Test
//import org.koin.module.Module
//import org.koin.test.koin.example.ServiceA
//import org.koin.test.koin.example.ServiceB
//
//
//class ScopedModule : Module() {
//    override fun onLoad() {
//        declareContext {
//            scope(ServiceA::class)
//            provide { ServiceA(get()) }
//        }
//    }
//
//}
//
//class NonScopedModule : Module() {
//    override fun onLoad() {
//        declareContext {
//            provide { ServiceB() }
//        }
//    }
//
//}
//
///**
// * Created by arnaud on 31/05/2017.
// */
//class ScopeTest {
//
//    @Test
//    fun `don't inject into instance`() {
//
//    }
//}