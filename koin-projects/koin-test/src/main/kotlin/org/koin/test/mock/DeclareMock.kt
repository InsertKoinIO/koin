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
package org.koin.test.mock

import org.koin.core.Koin
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.context.GlobalContext
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.RootScope
import org.koin.core.scope.Scope
import org.koin.core.time.measureDuration
import org.koin.ext.getFullName
import org.koin.test.KoinTest
import org.mockito.Mockito.mock
import kotlin.reflect.KClass

/**
 * Declare & Create a mock in Koin container for given type
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : Any> KoinTest.declareMock(
        qualifier: Qualifier? = null,
        noinline stubbing: (T.() -> Unit)? = null
): T = GlobalContext.get().koin.declareMock(qualifier, stubbing)

/**
 * Declare & Create a mock in Koin container for given type
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : Any> Koin.declareMock(
        qualifier: Qualifier? = null,
        noinline stubbing: (T.() -> Unit)? = null
): T = rootScope.declareMock(qualifier, stubbing)

/**
 * Declare & Create a mock in Koin container for given type and scope
 *
 * @author Pedro Moura
 */
inline fun <reified T: Any> Scope.declareMock(
        qualifier: Qualifier? = null,
        noinline stubbing: (T.() -> Unit)? = null
): T {
    val clazz = T::class
    val foundDefinition: BeanDefinition<Scope, T> = getDefinition(clazz, qualifier)
    declareMockDefinition(foundDefinition, stubbing)
    return get(qualifier)
}

@Suppress("UNCHECKED_CAST")
inline fun <S: Scope, reified T : Any> S.getDefinition(
        clazz: KClass<T>,
        qualifier: Qualifier?
): BeanDefinition<S, T> {
    logger.info("declare mock for '${clazz.getFullName()}'")

    return beanRegistry.findDefinition(qualifier, clazz) as BeanDefinition<S, T>?
            ?: throw NoBeanDefFoundException("No definition found for qualifier='$qualifier' & class='$clazz'")
}

inline fun <S: Scope, reified T: Any> S.declareMockDefinition(
        foundDefinition: BeanDefinition<S, T>,
        noinline stubbing: (T.() -> Unit)?
) {
    val definition: BeanDefinition<S, T> = foundDefinition.createMockedDefinition(stubbing)
    beanRegistry.saveDefinition(definition)
}

inline fun <S: Scope, reified T> BeanDefinition<S, T>.createMockedDefinition(noinline stubbing: (T.() -> Unit)? = null): BeanDefinition<S, T> {
    val definition: Definition<S, T> = {
        val (instance: T, time: Double) = measureDuration {
            mock(T::class.java)
        }
        logger.debug("| mock created in $time ms")
        stubbing?.let { instance.apply(stubbing) }
        instance
    }
    val copy = BeanDefinition(qualifier, scopeName, primaryType, this.kind, definition)
    copy.secondaryTypes = this.secondaryTypes
    copy.properties = this.properties.copy()
    copy.options = this.options.copy()
    copy.options.override = true
    copy.createInstanceHolder()
    return copy
}