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
import org.koin.core.KoinApplication
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.KoinComponent
import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Definition
import org.koin.core.error.NoBeanDefFoundException
import org.koin.test.check.checkModules
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
 * Check all definition's dependencies - run all modules in a test sandbox
 * and checkModules if definitions can run
 *
 * @param list of modules
 * @param logger - default is EmptyLogger
 */
fun KoinApplication.checkModules() = koin.checkModules()

/**
 * Declare & Create a mock in Koin container for given type
 */
inline fun <reified T : Any> KoinApplication.declareMock(
    name: String = "",
    noinline stubbing: (T.() -> Unit)? = null
): T {
    val clazz = T::class
    logger.info("[mock] declare mock for $clazz")

    val foundDefinition: BeanDefinition<T> = koin.beanRegistry.findDefinition(name, clazz) as BeanDefinition<T>?
            ?: throw NoBeanDefFoundException("No definition found for name='$name' & class='$clazz'")

    declareMockedDefinition(foundDefinition, clazz, koin)

    return applyStub(koin, stubbing)
}

inline fun <reified T : Any> applyStub(
    koin: Koin,
    noinline stubbing: (T.() -> Unit)?
): T {
    val instance: T = koin.get()
    stubbing?.let { instance.apply(stubbing) }
    return instance
}

inline fun <reified T : Any> declareMockedDefinition(
    foundDefinition: BeanDefinition<T>,
    clazz: KClass<T>,
    koin: Koin
) {
    val definition: BeanDefinition<T> =
        foundDefinition.cloneForOverride({ mock(clazz.java) }, true)

    koin.beanRegistry.saveDefinition(definition)
}

fun <T : Any> BeanDefinition<T>.cloneForOverride(definition: Definition<T>, override: Boolean): BeanDefinition<T> {
    val copy = this.copy()
    copy.secondaryTypes = this.secondaryTypes
    copy.definition = definition
    copy.attributes = this.attributes.copy()
    copy.options = this.options.copy()
    copy.options.override = override
    copy.kind = this.kind
    return copy
}