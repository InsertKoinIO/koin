/*
 * Copyright 2017-2018 the original author or authors.
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
@file:Suppress("unused")

package org.koin.test

import org.koin.core.Koin
import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.dsl.path.Path
import org.koin.log.Logger
import org.koin.log.PrintLogger
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.test.core.checkModules
import org.koin.test.ext.koin.dryRun
import org.mockito.Mockito.mock
import kotlin.reflect.KClass


/**
 * KoinTest main entry point
 *
 * @author Arnaud Giuliani
 */

/**
 * Koin Test Component
 */
interface KoinTest : KoinComponent

/**
 * Dry Run
 * Try to instantiate all definitions
 * @Deprecated
 */
@Deprecated("Please use the checkModules() function to checkModules your list of modules")
fun KoinTest.dryRun(parameters: ParameterDefinition = emptyParameterDefinition()) =
    (StandAloneContext.koinContext as KoinContext).dryRun(parameters)

/**
 * Check all definition's dependencies - run all modules in a test sandbox
 * and checkModules if definitions can run
 *
 * @param list of modules
 * @param logger - default is EmptyLogger
 */
fun KoinTest.checkModules(list: List<Module>, logger: Logger = PrintLogger()) =
    StandAloneContext.checkModules(list, logger)

/**
 * Declare & Create a mock in Koin container for given type
 */
inline fun <reified T : Any> KoinTest.declareMock(
    isFactory: Boolean = false,
    module: String? = null,
    binds: List<KClass<*>> = emptyList()
) {
    val clazz = T::class.java
    Koin.logger?.info("[mock] declare mock for $clazz")
    StandAloneContext.loadKoinModules(
        module(module ?: Path.ROOT) {
            val def = if (!isFactory) {
                single(override = true) {
                    mock<T>(clazz)
                }
            } else {
                factory(override = true) {
                    mock<T>(clazz)
                }
            }
            binds.forEach { def.bind(it) }
        }
    )
}

/**
 * Displays Module paths
 */
fun dumpModulePaths() {
    Koin.logger?.info("Module paths:")
    (StandAloneContext.koinContext as KoinContext).instanceRegistry.pathRegistry.paths.forEach {
        Koin.logger?.info(
            "[$it]"
        )
    }
}

/**
 * Declare a definition to be included in Koin container
 */
fun KoinTest.declare(module: String? = null, moduleExpression: ModuleDefinition.() -> Unit) {
    StandAloneContext.loadKoinModules(
        module(module ?: Path.ROOT) {
            moduleExpression()
        }
    )
}