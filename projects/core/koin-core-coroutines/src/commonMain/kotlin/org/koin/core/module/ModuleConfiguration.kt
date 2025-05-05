package org.koin.core.module

import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.lazyModules

/**
 * ModuleConfiguration - class to gather module declaration for regular and lazy modules, as a common unit
 * declare a consistent unit, representing all modules, and lazyModules
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@KoinDslMarker
class ModuleConfiguration(){

    @KoinInternalApi
    val _modules : ArrayList<Module> = arrayListOf()

    @KoinInternalApi
    val _lazyModules: ArrayList<LazyModule> = arrayListOf()

    @KoinInternalApi
    var _dispatcher : CoroutineDispatcher? = null
        private set

    fun modules(vararg module: Module) = _modules.addAll(module)
    fun modules(module: List<Module>) = _modules.addAll(module)
    fun lazyModules(vararg lazyModule : LazyModule) = _lazyModules.addAll(lazyModule)
    fun lazyModules(lazyModule : List<LazyModule>) = _lazyModules.addAll(lazyModule)
    fun dispatcher(dispatcher : CoroutineDispatcher? = null){
        _dispatcher = dispatcher
    }
}

/**
 * Declare a ModuleConfiguration configuration
 *
 * @see ModuleConfiguration
 */
@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
@KoinApplicationDslMarker
fun KoinApplication.moduleConfiguration(config : ModuleConfiguration.() -> Unit){
    val conf = ModuleConfiguration().also(config)
    moduleConfiguration(conf)
}

@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
@KoinApplicationDslMarker
fun KoinApplication.moduleConfiguration(config : ModuleConfiguration){
    modules(config._modules)
    lazyModules(config._lazyModules, config._dispatcher)
}

@KoinDslMarker
@KoinExperimentalAPI
fun moduleConfiguration(config : ModuleConfiguration.() -> Unit) : ModuleConfiguration = ModuleConfiguration().also(config)