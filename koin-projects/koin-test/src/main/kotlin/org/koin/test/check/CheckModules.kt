/*
 * Copyright 2017-2020 the original author or authors.
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
import org.koin.core.definition.BeanDefinition
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeDefinition
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

/**
 * Check all definition's dependencies - start all modules and check if definitions can run
 */
fun KoinApplication.checkModules(parameters: CheckParameters? = null) = koin.checkModules(parameters)

/**
 *
 */
fun checkModules(parameters: CheckParameters? = null, appDeclaration: KoinAppDeclaration) {
    koinApplication(appDeclaration).checkModules(parameters)
}

/**
 * Check all definition's dependencies - start all modules and check if definitions can run
 */
fun Koin.checkModules(parametersDefinition: CheckParameters? = null) {
    _logger.info("[Check] checking current modules ...")

    val allParameters = makeParameters(parametersDefinition)

    checkScopedDefinitions(allParameters)

    close()

    _logger.info("[Check] modules checked")
}

private fun Koin.makeParameters(parametersDefinition: CheckParameters?): MutableMap<CheckedComponent, ParametersCreator> {
    val bindings = ParametersBinding()
    bindings.koin = this
    parametersDefinition?.let {
        bindings.parametersDefinition()
    }
    val allParameters = bindings.creators
    return allParameters
}

private fun Koin.checkScopedDefinitions(allParameters: MutableMap<CheckedComponent, ParametersCreator>) {
    _scopeRegistry.scopeDefinitions.values.forEach { scopeDefinition ->
        runScope(scopeDefinition, allParameters)
    }
}

private fun Koin.runScope(scopeDefinition: ScopeDefinition, allParameters: MutableMap<CheckedComponent, ParametersCreator>) {
    val scope = getOrCreateScope(scopeDefinition.qualifier.value, scopeDefinition.qualifier)
    scope._scopeDefinition.definitions.forEach {
        runDefinition(allParameters, it, scope)
    }
}

private fun runDefinition(allParameters: MutableMap<CheckedComponent, ParametersCreator>, it: BeanDefinition<*>, scope: Scope) {
    val parameters = allParameters[CheckedComponent(it.qualifier, it.primaryType)]?.invoke(it.qualifier)
            ?: parametersOf()
    scope.get<Any>(it.primaryType, it.qualifier) { parameters }
}
