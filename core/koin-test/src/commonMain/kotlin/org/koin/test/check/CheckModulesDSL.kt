/*
 * Copyright 2017-2021 the original author or authors.
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
package org.koin.test.check

import org.koin.core.Koin
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools
import org.koin.test.mock.MockProvider
import kotlin.reflect.KClass

data class CheckedComponent(val qualifier: Qualifier? = null, val type: KClass<*>)

class ParametersBinding(val koin: Koin) {

    val parametersCreators = mutableMapOf<CheckedComponent, ParametersCreator>()
    val defaultValues = mutableMapOf<String, Any>()

    @Deprecated("use withParameter() instead", ReplaceWith("withParameter(qualifier,creator)"))
    inline fun <reified T> create(qualifier: Qualifier? = null, noinline creator: ParametersCreator) =
        parametersCreators.put(CheckedComponent(qualifier, T::class), creator)

    inline fun <reified T> withParameter(qualifier: Qualifier? = null, noinline creator: ParametersInstance) =
        parametersCreators.put(CheckedComponent(qualifier, T::class)) { q -> parametersOf(creator(q)) }

    @Deprecated("use withParameter() instead", ReplaceWith("withParameter(clazz,qualifier,creator)"))
    fun create(clazz: KClass<*>, qualifier: Qualifier? = null, creator: ParametersCreator) =
        parametersCreators.put(CheckedComponent(qualifier, clazz), creator)

    fun withParameter(clazz: KClass<*>, qualifier: Qualifier? = null, creator: ParametersInstance) =
        parametersCreators.put(CheckedComponent(qualifier, clazz)) { q -> parametersOf(creator(q)) }

    @Deprecated("use withInstance() instead", ReplaceWith("withInstance(t)"))
    inline fun <reified T : Any> defaultValue(t: T) = defaultValues.put(KoinPlatformTools.getClassName(T::class), t)

    @Deprecated("use withInstance() instead", ReplaceWith("withInstance()"))
    inline fun <reified T : Any> defaultValue() =
        defaultValues.put(KoinPlatformTools.getClassName(T::class), MockProvider.makeMock<T>())

    inline fun <reified T : Any> withInstance(t: T) = defaultValues.put(KoinPlatformTools.getClassName(T::class), t)
    inline fun <reified T : Any> withInstance() =
        defaultValues.put(KoinPlatformTools.getClassName(T::class), MockProvider.makeMock<T>())

    fun withProperty(key: String, value: Any) = koin.setProperty(key, value)
}

typealias ParametersCreator = (Qualifier?) -> ParametersHolder
typealias ParametersInstance = (Qualifier?) -> Any
typealias CheckParameters = ParametersBinding.() -> Unit