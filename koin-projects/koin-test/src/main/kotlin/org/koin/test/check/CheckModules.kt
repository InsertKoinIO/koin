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
import org.koin.core.parameter.parametersOf

/**
 * Check all definition's dependencies - start all nodules and check ifdefinitions can run
 */
fun KoinApplication.checkModules(parameters: CheckParameters? = null) = koin.checkModules(parameters)

/**
 * Check all definition's dependencies - start all nodules and check if definitions can run
 */
fun Koin.checkModules(parametersDefinition: CheckParameters? = null) {
    val bindings = ParametersBinding()
    bindings.koin = this
    parametersDefinition?.let {
        bindings.parametersDefinition()
    }
    val allParameters = bindings.creators

    checkMainDefinitions(allParameters)

    checkScopedDefinitions(allParameters)

    close()
}

private fun Koin.checkScopedDefinitions(allParameters: MutableMap<CheckedComponent, ParametersCreator>) {
    scopeRegistry.getScopeSets().forEach { set ->
        val scope = createScope(set.qualifier.toString(), set.qualifier)
        set.definitions.forEach {
            val parameters = allParameters[CheckedComponent(it.qualifier, it.primaryType)]?.invoke(it.qualifier)
                    ?: parametersOf()
            scope.get<Any>(it.primaryType, it.qualifier) { parameters }
        }
    }
}

private fun Koin.checkMainDefinitions(allParameters: MutableMap<CheckedComponent, ParametersCreator>) {
    rootScope.beanRegistry.getAllDefinitions().forEach {
        val parameters = allParameters[CheckedComponent(it.qualifier, it.primaryType)]?.invoke(it.qualifier)
                ?: parametersOf()
        get<Any>(it.primaryType, it.qualifier) { parameters }
    }
}