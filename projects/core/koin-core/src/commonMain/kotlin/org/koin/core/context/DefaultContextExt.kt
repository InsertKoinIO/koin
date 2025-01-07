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
package org.koin.core.context

import org.koin.core.KoinApplication
import org.koin.core.module.KoinApplicationDslMarker
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.KoinConfiguration
import org.koin.mp.KoinPlatformTools

/**
 * Starter function to help start Koin context with default context parameters
 *
 * @author Arnaud Giuliani
 */

/**
 * Start a Koin Application as StandAlone
 */
@KoinApplicationDslMarker
fun startKoin(koinApplication: KoinApplication): KoinApplication = KoinPlatformTools.defaultContext().startKoin(koinApplication)

/**
 * Start a Koin Application as StandAlone
 */
@KoinApplicationDslMarker
fun startKoin(appDeclaration: KoinAppDeclaration): KoinApplication = KoinPlatformTools.defaultContext().startKoin(appDeclaration)

@KoinApplicationDslMarker
fun startKoin(appConfiguration: KoinConfiguration): KoinApplication = KoinPlatformTools.defaultContext().startKoin(appConfiguration.config)

/**
 * Stop current StandAlone Koin application
 */
fun stopKoin() = KoinPlatformTools.defaultContext().stopKoin()

/**
 * load Koin module in global Koin context
 */
fun loadKoinModules(module: Module) = KoinPlatformTools.defaultContext().loadKoinModules(module)

/**
 * load Koin modules in global Koin context
 */
fun loadKoinModules(modules: List<Module>) = KoinPlatformTools.defaultContext().loadKoinModules(modules)

/**
 * unload Koin modules from global Koin context
 */
fun unloadKoinModules(module: Module) = KoinPlatformTools.defaultContext().unloadKoinModules(module)

/**
 * unload Koin modules from global Koin context
 */
fun unloadKoinModules(modules: List<Module>) = KoinPlatformTools.defaultContext().unloadKoinModules(modules)
