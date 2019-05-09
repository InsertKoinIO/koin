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
package org.koin.test.check

import org.koin.core.Koin
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

data class CheckedComponent(val qualifier: Qualifier? = null, val type: KClass<*>)

class ParametersBinding {
    val creators = mutableMapOf<CheckedComponent, ParametersCreator>()
    lateinit var koin: Koin
    inline fun <reified T> create(qualifier: Qualifier? = null, noinline creator: ParametersCreator) =
            creators.put(CheckedComponent(qualifier, T::class), creator)
}

typealias ParametersCreator = (Qualifier?) -> DefinitionParameters
typealias CheckParameters = ParametersBinding.() -> Unit