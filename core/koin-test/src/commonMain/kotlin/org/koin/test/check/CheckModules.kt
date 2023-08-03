/*
 * Copyright 2017-Present the original author or authors.
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
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.definition.BeanDefinition
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatformTools
import org.koin.test.mock.MockProvider
import org.koin.test.parameter.MockParameter

/**
 * Check all definition's dependencies - start all modules and check if definitions can run
 */
fun KoinApplication.checkModules(parameters: CheckParameters? = null) = koin.checkModules(parameters)

/**
 * Verify Modules by running each definition
 *
 * @param level - Log level
 * @param parameters - parameter setup
 * @param appDeclaration - koin Application
 */
fun checkModules(level: Level = Level.INFO, parameters: CheckParameters? = null, appDeclaration: KoinAppDeclaration) {
    startKoin(appDeclaration)
        .logger(KoinPlatformTools.defaultLogger(level))
        .checkModules(parameters)
}

/**
 * Check given modules directly
 *
 * @param modules - list of modules
 * @param appDeclaration - Koin app config if needed
 * @param parameters - Check parameters DSL
 */
fun checkKoinModules(modules : List<Module>, appDeclaration: KoinAppDeclaration = {}, parameters: CheckParameters? = null) {
    startKoin(appDeclaration)
        .modules(modules)
        .checkModules(parameters)
    stopKoin()
}

/**
 * @see checkModules
 *
 * Deprecated
 */
@Deprecated("use instead checkKoinModules(modules : List<Module>, appDeclaration: KoinAppDeclaration = {}, parameters: CheckParameters? = null)")
fun checkKoinModules(level: Level = Level.INFO, parameters: CheckParameters? = null, appDeclaration: KoinAppDeclaration) {
    startKoin(appDeclaration)
        .logger(KoinPlatformTools.defaultLogger(level))
        .checkModules(parameters)
    stopKoin()
}

/**
 * @see checkKoinModules
 *
 * Deprecated
 */
@Deprecated("use instead checkKoinModules(modules : List<Module>, appDeclaration: KoinAppDeclaration = {}, parameters: CheckParameters? = null)")
fun checkKoinModules(vararg modules : Module, level: Level = Level.INFO, parameters: CheckParameters? = null) {
    startKoin({})
        .logger(KoinPlatformTools.defaultLogger(level))
        .checkModules(parameters)
    stopKoin()
}

/**
 * Check all definition's dependencies - start all modules and check if definitions can run
 */
fun Koin.checkModules(parametersDefinition: CheckParameters? = null) {
    logger.info("[Check] checking modules ...")

    checkAllDefinitions(
        declareParameterCreators(parametersDefinition)
    )

    logger.info("[Check] All checked")
    close()
}

private fun Koin.declareParameterCreators(parametersDefinition: CheckParameters?) =
    ParametersBinding(this).also { binding -> parametersDefinition?.invoke(binding) }

@OptIn(KoinInternalApi::class)
private fun Koin.checkAllDefinitions(allParameters: ParametersBinding) {
    val scopes: List<Scope> = instantiateAllScopes(allParameters)
    allParameters.scopeLinks.forEach { scopeLink ->
        val linkTargets = scopes.filter { it.scopeQualifier == scopeLink.value }
        scopes.filter { it.scopeQualifier == scopeLink.key }
                .forEach { scope -> linkTargets.forEach { linkTarget -> scope.linkTo(linkTarget) } }
    }
    instanceRegistry.instances.values.toSet().forEach { factory ->
        checkDefinition(allParameters, factory.beanDefinition, scopes.first { it.scopeQualifier == factory.beanDefinition.scopeQualifier })
    }
}

@OptIn(KoinInternalApi::class)
private fun Koin.instantiateAllScopes(allParameters: ParametersBinding): List<Scope> {
    return scopeRegistry.scopeDefinitions.map { qualifier ->
        val sourceScopeValue = mockSourceValue(qualifier)
        getOrCreateScope(qualifier.value, qualifier, sourceScopeValue)
    }
}

private fun mockSourceValue(qualifier: Qualifier): Any? {
    return if (qualifier is TypeQualifier) {
        MockProvider.makeMock(qualifier.type)
    } else null
}

private fun Koin.checkDefinition(
    allParameters: ParametersBinding,
    definition: BeanDefinition<*>,
    scope: Scope
) {
    val parameters: ParametersHolder = allParameters.parametersCreators[CheckedComponent(
        definition.qualifier,
        definition.primaryType
    )]?.invoke(
        definition.qualifier
    ) ?: MockParameter(scope, allParameters.defaultValues)
    logger.info("[Check] definition: $definition")
    scope.get<Any>(definition.primaryType, definition.qualifier) { parameters }

    for (secondaryType in definition.secondaryTypes) {
        val valueAsSecondary = scope.get<Any>(secondaryType, definition.qualifier) { parameters }
        require(secondaryType.isInstance(valueAsSecondary)) {
            "instance of ${valueAsSecondary::class} is not inheritable from $secondaryType"
        }
    }
}
