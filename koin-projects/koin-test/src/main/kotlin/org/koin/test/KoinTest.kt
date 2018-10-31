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
import org.koin.core.bean.BeanRegistry
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.dsl.path.Path
import org.koin.error.NoBeanDefFoundException
import org.koin.log.Logger
import org.koin.log.PrintLogger
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.test.core.checkModules
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
fun KoinTest.dryRun() =
    StandAloneContext.getKoin().createEagerInstances()

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
    name: String = "",
    noinline stubbing: (T.() -> Unit)? = null
): T {
    val clazz = T::class
    Koin.logger.info("[mock] declare mock for $clazz")

    val koin = StandAloneContext.getKoin()

    val foundDefinition: BeanDefinition<*> = getDefinition(koin, name, clazz)

    declareMockedDefinition(foundDefinition, clazz, koin)

    return mockInstance(koin, stubbing)
}

inline fun <reified T : Any> mockInstance(
    koin: Koin,
    noinline stubbing: (T.() -> Unit)?
): T {
    val instance: T = koin.koinContext.get<T>()
    stubbing?.let { instance.apply(stubbing) }
    return instance
}

inline fun <reified T : Any> declareMockedDefinition(
    foundDefinition: BeanDefinition<*>,
    clazz: KClass<T>,
    koin: Koin
) {
    val definition: BeanDefinition<*> =
        foundDefinition.copy(definition = { mock(clazz.java) }, allowOverride = true)
    koin.declare(definition)
}

inline fun <reified T : Any> getDefinition(
    koin: Koin,
    name: String,
    clazz: KClass<T>
): BeanDefinition<*> {
    val beanRegistry = koin.koinContext.instanceRegistry.beanRegistry
    val definitions = lookAtDefinition(name, beanRegistry, clazz)
    return if (definitions.size == 1) definitions.first() else throw NoBeanDefFoundException("Can't find definition for '$clazz' to mock")
}

inline fun <reified T : Any> lookAtDefinition(
    name: String,
    beanRegistry: BeanRegistry,
    clazz: KClass<T>
): List<BeanDefinition<*>> =
    if (name.isNotEmpty()) beanRegistry.searchByName(
        name
    ) else beanRegistry.searchByClass(clazz)

/**
 * Displays Module paths
 */
fun dumpModulePaths() {
    Koin.logger.info("Module paths:")
    StandAloneContext.getKoin().koinContext.instanceRegistry.pathRegistry.paths.forEach {
        Koin.logger.info(
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