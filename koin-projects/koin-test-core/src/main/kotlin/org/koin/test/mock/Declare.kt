package org.koin.test.mock

import org.koin.core.context.KoinContextHandler
import org.koin.core.qualifier.Qualifier
import org.koin.test.KoinTest
import org.koin.test.get

///**
// * Declare component on the fly
// * @param moduleDeclaration lambda
// */
//fun KoinTest.declareModule(moduleDeclaration: ModuleDeclaration) {
//    val module = Module(createAtStart = false, override = true)
//    val koin = KoinContextHandler.get()
//    module._rootScope = koin._scopeRegistry.rootScopeDefinition
//    moduleDeclaration(module)
//    koin.loadModules(listOf(module))
//}


inline fun <reified T : Any> KoinTest.declare(
    qualifier: Qualifier? = null,
    noinline instance: () -> T
): T {
    val koin = KoinContextHandler.get()
    koin.declare(instance(), qualifier, override = true)
    return get(qualifier)
}