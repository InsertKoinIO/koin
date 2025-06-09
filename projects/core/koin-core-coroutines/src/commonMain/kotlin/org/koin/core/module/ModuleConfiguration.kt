/*
 * Copyright 2017-Present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * Declare a ModuleConfiguration configuration in KoinApplication
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

/**
 * Declare a ModuleConfiguration configuration in KoinApplication
 *
 * @see ModuleConfiguration
 */
@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
@KoinApplicationDslMarker
fun KoinApplication.moduleConfiguration(config : ModuleConfiguration){
    modules(config._modules)
    lazyModules(config._lazyModules, config._dispatcher)
}

/**
 * Declare a ModuleConfiguration configuration
 *
 * @see ModuleConfiguration
 */
@KoinDslMarker
@KoinExperimentalAPI
fun moduleConfiguration(config : ModuleConfiguration.() -> Unit) : ModuleConfiguration = ModuleConfiguration().also(config)