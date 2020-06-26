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
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeDefinition
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication
import org.koin.test.parameter.MockParameter
import org.mockito.Mockito

/**
 * Check all definition's dependencies - start all modules and check if definitions can run
 */
fun KoinApplication.checkModules(parameters: CheckParameters? = null) = koin.checkModules(parameters)

/**
 *
 */
fun checkModules(level: Level = Level.INFO, parameters: CheckParameters? = null, appDeclaration: KoinAppDeclaration) {
    koinApplication(appDeclaration)
        .logger(PrintLogger(level))
        .checkModules(parameters)
}

/**
 * Check all definition's dependencies - start all modules and check if definitions can run
 */
fun Koin.checkModules(parametersDefinition: CheckParameters? = null) {
    _logger.info("[Check] checking current modules ...")

    checkScopedDefinitions(declareParameterCreators(parametersDefinition))

    close()

    _logger.info("[Check] modules checked")
}

private fun Koin.declareParameterCreators(
    parametersDefinition: CheckParameters?
): ParametersBinding {
    val bindings = ParametersBinding(this)
    parametersDefinition?.let { bindings.parametersDefinition() }
    return bindings
}

private fun Koin.checkScopedDefinitions(allParameters: ParametersBinding) {
    _scopeRegistry.scopeDefinitions.values.forEach { scopeDefinition ->
        checkScope(scopeDefinition, allParameters)
    }
}

private fun Koin.checkScope(
    scopeDefinition: ScopeDefinition,
    allParameters: ParametersBinding
) {
    val qualifier = scopeDefinition.qualifier
    val sourceScopeValue = mockSourceValue(qualifier)
    val scope = getOrCreateScope(qualifier.value, qualifier, sourceScopeValue)
    scope._scopeDefinition.definitions.forEach {
        checkDefinition(allParameters, it, scope)
    }
}

private fun mockSourceValue(qualifier: Qualifier): Any? {
    return if (qualifier is TypeQualifier) {
        Mockito.mock(qualifier.type.java)
    } else null
}

private fun checkDefinition(
    allParameters: ParametersBinding,
    definition: BeanDefinition<*>,
    scope: Scope
) {
    val parameters = allParameters.parametersCreators[CheckedComponent(definition.qualifier,
        definition.primaryType)]?.invoke(
        definition.qualifier)
        ?: MockParameter(scope, allParameters.defaultValues) //TODO add default param check here
    scope.get<Any>(definition.primaryType, definition.qualifier) { parameters }
}
