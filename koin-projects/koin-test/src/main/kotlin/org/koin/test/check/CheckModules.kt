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
import org.koin.core.KoinApplication
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.getScopeName
import kotlin.reflect.KClass

/**
 * Check all definition's dependencies - start all nodules and check ifdefinitions can run
 */
fun KoinApplication.checkModules(
        parameterCreators: Map<NamedKClass, ParametersCreator> = mapOf()) = koin.checkModules(parameterCreators)

/**
 * Check all definition's dependencies - start all nodules and check if definitions can run
 */
fun Koin.checkModules(
        parameterCreators: Map<NamedKClass, ParametersCreator> = mapOf()) {

    beanRegistry.getAllDefinitions().forEach {
        val scope = if (it.isScoped()) createScope(it.getScopeName().toString(), it.getScopeName()) else null
        val parameters = parameterCreators[NamedKClass(it.qualifier, it.primaryType)]?.invoke(it.qualifier)
                ?: parametersOf()
        get<Any>(it.primaryType, it.qualifier, scope ?: Scope.GLOBAL) { parameters }
        scope?.close()
    }
    close()
}

data class NamedKClass(val qualifier: Qualifier? = null, val type: KClass<*>)
typealias ParametersCreator = (Qualifier?) -> DefinitionParameters

class ParametersBinding {
    val creators = mutableMapOf<NamedKClass, ParametersCreator>()
    inline fun <reified T> create(qualifier: Qualifier? = null, noinline creator: ParametersCreator) =
            creators.put(NamedKClass(qualifier, T::class), creator)
}

fun parameterCreatorsOf(f: ParametersBinding.() -> Unit): Map<NamedKClass, ParametersCreator> {
    return ParametersBinding().apply(f).creators
}
