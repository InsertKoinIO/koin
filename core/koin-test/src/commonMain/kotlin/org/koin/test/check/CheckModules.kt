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
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.error.*
import org.koin.core.logger.Level
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatformTools
import org.koin.test.mock.MockProvider
import kotlin.reflect.KClass

/**
 * Check all definition's dependencies - start all modules and check if definitions can run
 */
fun KoinApplication.checkModules(
    defaultValues: Map<KClass<*>, Any> = hashMapOf(),
    allowedMocks: List<KClass<*>> = listOf(),
    allowedExceptions: List<KClass<*>> = listOf()
) = koin.checkModules(defaultValues, allowedMocks, allowedExceptions)

/**
 *
 */
fun checkModules(
    level: Level = Level.INFO,
    defaultValues: Map<KClass<*>, Any> = hashMapOf(),
    allowedMocks: List<KClass<*>> = listOf(),
    allowedExceptions: List<KClass<*>> = listOf(),
    appDeclaration: KoinAppDeclaration
) {
    startKoin(appDeclaration)
        .logger(KoinPlatformTools.defaultLogger(level))
        .checkModules(defaultValues, allowedMocks, allowedExceptions)
}

/**
 * Check all definition's dependencies - start all modules and check if definitions can run
 */
@OptIn(KoinInternalApi::class)
fun Koin.checkModules(
    defaultValues: Map<KClass<*>, Any> = hashMapOf(),
    allowedMocks: List<KClass<*>> = listOf(),
    allowedExceptions: List<KClass<*>> = listOf()
) {
    logger.info("[Check] checking current modules ...")

    // Extract all definitions to check
    val definitions = instanceRegistry.instances.values.map { it.beanDefinition }.distinct()

    // Reconfigure with MockInstanceFactory
    instanceRegistry.instances.forEach { (mapping, factory) ->
        instanceRegistry.saveMapping(
            allowOverride = true,
            mapping = mapping,
            factory = MockInstanceFactory(factory.beanDefinition),
            logWarning = false
        )
    }

    // Execute resolution
    definitions.forEach { beanDefinition ->
        logger.info("Checking $beanDefinition ...")
        var source: Pair<KClass<*>, Any>? = null
        val scope = if (beanDefinition.scopeQualifier != scopeRegistry.rootScope.scopeQualifier) {
            source =
                (beanDefinition.scopeQualifier as? TypeQualifier)?.let { Pair(it.type, MockProvider.makeMock(it.type)) }
            getOrCreateScope(beanDefinition.scopeQualifier.value, beanDefinition.scopeQualifier, source)
        } else scopeRegistry.rootScope
        try {
            val allValues = source?.let { defaultValues + it } ?: defaultValues
            beanDefinition.definition.invoke(scope, MockParametersHolder(allValues, allowedMocks))
        } catch (e: Exception) {
            val errorClass = e::class
            if (errorClass in breakingExceptions || errorClass !in allowedExceptions) {
                throw BrokenDefinitionException("Definition is broken: $beanDefinition \n- due to error: $e")
            } else logger.error("Intercepted Error: $e")
            e.printStackTrace()
        }
        if (!scope.isRoot) {
            scope.close()
        }
    }

    close()
    logger.info("All definitions are OK!")
}

val breakingExceptions = listOf<KClass<*>>(
    NoBeanDefFoundException::class,
    NoScopeDefFoundException::class,
    InstanceCreationException::class,
    DefinitionParameterException::class
)
