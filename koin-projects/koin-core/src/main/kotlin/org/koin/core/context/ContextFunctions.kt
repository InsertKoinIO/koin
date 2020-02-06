package org.koin.core.context

import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration


/**
 * Start a Koin Application as StandAlone
 */
fun startKoin(koinContext: KoinContext = GlobalContext(), koinApplication: KoinApplication): KoinApplication {
    KoinContextHandler.register(koinContext)
    KoinContextHandler.start(koinApplication)
    koinApplication.createEagerInstances()
    return koinApplication
}

/**
 *
 */
fun startKoin(koinContext: KoinContext = GlobalContext(), appDeclaration: KoinAppDeclaration): KoinApplication {
    KoinContextHandler.register(koinContext)
    val koinApplication = KoinApplication.init()
    KoinContextHandler.start(koinApplication)
    appDeclaration(koinApplication)
    koinApplication.createEagerInstances()
    return koinApplication
}

/**
 * Stop current StandAlone Koin application
 */
fun stopKoin() = KoinContextHandler.stop()

/**
 * load Koin module in global Koin context
 */
fun loadKoinModules(module: Module) {
    KoinContextHandler.get().loadModules(listOf(module))
}

/**
 * load Koin modules in global Koin context
 */
fun loadKoinModules(modules: List<Module>) {
    KoinContextHandler.get().loadModules(modules)
}

/**
 * unload Koin modules from global Koin context
 */
fun unloadKoinModules(module: Module) {
    KoinContextHandler.get().unloadModules(listOf(module))
}

/**
 * unload Koin modules from global Koin context
 */
fun unloadKoinModules(modules: List<Module>) {
    KoinContextHandler.get().unloadModules(modules)
}