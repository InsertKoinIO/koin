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
import org.koin.core.bean.BeanDefinition
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.standalone.StandAloneKoinApplication
import org.koin.core.time.measureDuration
import org.koin.ext.getFullName
import org.koin.test.KoinTest
import org.mockito.Mockito.mock

/**
 * Declare & Create a mock in Koin container for given type
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : Any> KoinTest.declareMock(
    name: String = "",
    noinline stubbing: (T.() -> Unit)? = null
): T {
    val koin = StandAloneKoinApplication.get().koin

    val clazz = T::class
    logger.info("declare mock for '${clazz.getFullName()}'")

    val foundDefinition: BeanDefinition<T> = koin.beanRegistry.findDefinition(name, clazz) as BeanDefinition<T>?
            ?: throw NoBeanDefFoundException("No definition found for name='$name' & class='$clazz'")

    koin.declareMockedDefinition(foundDefinition)

    return koin.applyStub(stubbing)
}

/**
 * Declare & Create a mock in Koin container for given type
 *
 * @author Arnaud Giuliani
 */
inline fun <reified T : Any> Koin.declareMock(
    name: String = "",
    noinline stubbing: (T.() -> Unit)? = null
): T {

    val clazz = T::class
    logger.info("declare mock for '${clazz.getFullName()}'")

    val foundDefinition: BeanDefinition<T> = beanRegistry.findDefinition(name, clazz) as BeanDefinition<T>?
            ?: throw NoBeanDefFoundException("No definition found for name='$name' & class='$clazz'")

    declareMockedDefinition(foundDefinition)

    return applyStub(stubbing)
}

inline fun <reified T : Any> Koin.applyStub(
    noinline stubbing: (T.() -> Unit)?
): T {
    val instance: T = get()
    stubbing?.let { instance.apply(stubbing) }
    return instance
}

inline fun <reified T : Any> Koin.declareMockedDefinition(
    foundDefinition: BeanDefinition<T>
) {
    val definition: BeanDefinition<T> = foundDefinition.cloneForMock()
    beanRegistry.saveDefinition(definition)
}

inline fun <reified T : Any> BeanDefinition<T>.cloneForMock(): BeanDefinition<T> {
    val copy = this.copy()
    copy.secondaryTypes = this.secondaryTypes
    copy.definition = {
        val (instance: T, time: Double) = measureDuration {
            mock(T::class.java)
        }
        logger.debug("| mock created in $time ms")
        instance
    }
    copy.attributes = this.attributes.copy()
    copy.options = this.options.copy()
    copy.options.override = true
    copy.kind = this.kind
    copy.createInstanceHolder()
    return copy
}
