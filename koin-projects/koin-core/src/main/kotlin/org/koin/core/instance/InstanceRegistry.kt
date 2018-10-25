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
package org.koin.core.instance

import org.koin.core.Koin
import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.holder.Instance
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.path.PathRegistry
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeRegistry
import org.koin.core.scope.getScope
import org.koin.core.stack.ResolutionStack
import org.koin.core.time.logDuration
import org.koin.core.time.measureDuration
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.ClosedScopeException
import org.koin.ext.fullname
import kotlin.reflect.KClass

/**
 * Instance Resolver
 *
 * @author Arnaud Giuliani
 */
class InstanceRegistry(
    val beanRegistry: BeanRegistry,
    val instanceFactory: InstanceFactory,
    val pathRegistry: PathRegistry,
    private val scopeRegistry: ScopeRegistry
) {

    private val resolutionStack = ResolutionStack()

    /**
     * resolve instance from InstanceRequest
     */
    fun <T : Any> resolve(request: InstanceRequest): T {
        return request.run {
            val search = when {
                name.isNotEmpty() -> {
                    { beanRegistry.searchByNameAndClass(name, clazz) }
                }
                else -> {
                    { beanRegistry.searchByClass(clazz) }
                }
            }
            proceedResolution(clazz, scope, parameters, search)
        }
    }

    /**
     * Resolve a dependency for its bean definition
     * @param scope - associated scope
     * @param clazz - Class
     * @param parameters - Parameters
     * @param definitionResolver - function to find bean definitions
     */
    private fun <T : Any> proceedResolution(
        clazz: KClass<*>,
        scope: Scope?,
        parameters: ParameterDefinition,
        definitionResolver: () -> List<BeanDefinition<*>>
    ): T = synchronized(this) {

        var resultInstance: T? = null
        val clazzName = clazz.fullname()

        val logIndent = resolutionStack.indent()

        val startChar = if (resolutionStack.isEmpty()) "+" else "+"
        Koin.logger.info("$logIndent$startChar-- '$clazzName'")

        val duration = measureDuration {
            try {
                val beanDefinition: BeanDefinition<T> =
                    logDuration("$logIndent|-- find definition") {
                        findDefinition(scope, definitionResolver)
                    }

                val targetScope: Scope? =
                    getTargetScope(beanDefinition, scope)

                val (instance, created) = logDuration("$logIndent|-- get instance") {
                    resolveInstance(
                        beanDefinition,
                        parameters,
                        targetScope)
                }
                resultInstance = instance

                // Log creation
                if (created) {
                    Koin.logger.info("$logIndent\\-- (*) Created")
                }
            } catch (e: Exception) {
                resolutionStack.clear()
                Koin.logger.err("Error while resolving instance for class '$clazzName' - error: $e ")
                throw e
            }
        }

        Koin.logger.debug("$logIndent!-- [$clazzName] resolved in $duration ms")

        return if (resultInstance != null) resultInstance!! else error("Could not create instance for $clazzName")
    }

    private fun <T : Any> findDefinition(
        scope: Scope?,
        definitionResolver: () -> List<BeanDefinition<*>>
    ): BeanDefinition<T> {
        return beanRegistry.retrieveDefinition(
            scope,
            definitionResolver,
            resolutionStack.last()
        )
    }

    private fun <T> resolveInstance(
        beanDefinition: BeanDefinition<T>,
        parameters: ParameterDefinition,
        targetScope: Scope?
    ): Instance<T> {
        return resolutionStack.resolve(beanDefinition) {
            instanceFactory.retrieveInstance(
                    beanDefinition,
                    parameters,
                    targetScope
                )
            }
        }

    private fun <T : Any> getTargetScope(
        beanDefinition: BeanDefinition<T>,
        scope: Scope?
    ): Scope? {
        return if (scope != null){
            if (isScopeRegistered(scope)) scope
            else throw ClosedScopeException("No open scoped '${scope.id}'")
        } else {
            val associatedScopeId = beanDefinition.getScope()
            return scope ?: scopeRegistry.getScope(associatedScopeId)
        }
    }

    private fun isScopeRegistered(scope: Scope) =
        scopeRegistry.getScope(scope.id) != null || scopeRegistry.getDetachScope(scope.uuid) != null

    /**
     * Create instances at start - tagged eager
     * @param defaultParameters
     */
    fun createEagerInstances(defaultParameters: ParameterDefinition) {
        val definitions = beanRegistry.definitions.filter { it.isEager }

        if (definitions.isNotEmpty()) {
            Koin.logger.info("Creating instances ...")
            createInstances(definitions, defaultParameters)
        }
    }

    /**
     * Create instances for given definition list & params
     * @param definitions
     * @param params
     */
    fun createInstances(
        definitions: Collection<BeanDefinition<*>>,
        params: ParameterDefinition
    ) {
        definitions.forEach { def ->
            proceedResolution(
                def.primaryType,
                null,
                params
            ) { listOf(def) }
        }
    }

    /**
     * Close res
     */
    fun close() {
        resolutionStack.clear()
        instanceFactory.clear()
        beanRegistry.clear()

    }
}