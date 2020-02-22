/*
 * Copyright 2017-2020 the original author or authors.
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