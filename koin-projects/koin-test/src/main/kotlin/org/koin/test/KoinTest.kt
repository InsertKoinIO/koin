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
import org.koin.dsl.context.ModuleDefinition
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext
import org.koin.test.core.check
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
 * Check all definition's dependencies
 */
fun KoinTest.check(list: List<Module>) = StandAloneContext.check(list)

/**
 * Declare & Create a mock in Koin container for given type
 */
inline fun <reified T : Any> KoinTest.createMock(
    isFactory: Boolean = false,
    binds: List<KClass<*>> = emptyList()
) {
    val clazz = T::class.java
    Koin.logger.info("<> declare mock for $clazz")
    StandAloneContext.loadKoinModules(
        module {
            val def = if (!isFactory) {
                single(override = true) {
                    createMock(clazz)
                }
            } else {
                factory(override = true) {
                    createMock(clazz)
                }
            }
            binds.forEach { def.bind(it) }
        }
    )
}

/**
 * Create & info mock
 */
fun <T : Any> createMock(clazz: Class<T>): T {
    Koin.logger.info("Create mock for $clazz")
    return mock<T>(clazz)
}

/**
 * Declare a definition to be included in Koin container
 */
fun KoinTest.declare(moduleExpression: ModuleDefinition.() -> Unit) {
    StandAloneContext.loadKoinModules(
        module {
            moduleExpression()
        }
    )
}