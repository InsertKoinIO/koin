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
package org.koin.core

import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceFactory
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.path.PathRegistry
import org.koin.core.stack.ResolutionStack
import org.koin.dsl.definition.BeanDefinition

/**
 * Instance Resolver
 *
 * @author Arnaud Giuliani
 */
class InstanceResolver(
    val beanRegistry: BeanRegistry,
    val instanceFactory: InstanceFactory,
    val pathRegistry: PathRegistry
) {

    private val resolutionStack = ResolutionStack()


    fun <T> resolve(req: InstanceRequest): T {
        return req.run {
            when {
                name.isNotEmpty() -> proceedResolution(module, clazz.java, parameters) {
                    beanRegistry.search(name, clazz)
                }
                else -> proceedResolution(module, clazz.java, parameters) { beanRegistry.searchAll(clazz) }
            }
        }
    }

    fun <T> resolve(req: ClassRequest): T {
        return proceedResolution(req.module, req.clazz, req.parameters) {
            beanRegistry.search(req.name, req.clazz)
        }
    }


//    /**
//     * Resolve a dependency for its bean definition
//     * @param canonicalName bean definition canonicalName
//     */
//    inline fun <reified T> resolve(
//        canonicalName: String,
//        module: String? = null,
//        noinline parameters: ParameterDefinition
//    ): T =
//        proceedResolution(module, T::class, parameters) { beanRegistry.search(canonicalName, T::class) }
//
//    /**
//     * Resolve a dependency for its bean definition
//     * byt its type
//     */
//    fun <T> resolve(
//        clazz: KClass<*>,
//        module: String? = null,
//        parameters: ParameterDefinition
//    ): T = proceedResolution(module, clazz, parameters) { beanRegistry.searchAll(clazz) }
//
//
//    /**
//     * Resolve a dependency for its bean definition
//     * by its inferred type
//     */
//    inline fun <reified T> resolve(module: String? = null, noinline parameters: ParameterDefinition): T =
//        resolve(T::class, module, parameters)

//    /**
//     * Resolve instance from
//     */
//    fun <T> resolveInstanceFromDefinitions(
//        foundDefinitions: List<BeanDefinition<*>>,
//        module: String?,
//        parameters: ParameterDefinition
//    ): T {
//        return when (foundDefinitions.size) {
//            0 -> throw NoBeanDefFoundException("No bean beanDefinition found")
//            1 -> {
//                val def = foundDefinitions.first()
//                proceedResolution(module, def.clazz, parameters) { listOf(def) }
//            }
//            else -> throw NoBeanDefFoundException("Multiple bean definitions found")
//        }
//    }

    /**
     * Resolve a dependency for its bean definition
     * @param module - module path
     * @param clazz - Class
     * @param parameters - Parameters
     * @param definitionResolver - function to find bean definitions
     */
    fun <T> proceedResolution(
        module: String? = null,
        clazz: Class<*>,
        parameters: ParameterDefinition,
        definitionResolver: () -> List<BeanDefinition<*>>
    ): T = synchronized(this) {

        val clazzName = clazz.canonicalName

        var resultInstance: T? = null

        try {
            val beanDefinition: BeanDefinition<*> =
                beanRegistry.getVisibleBean(
                    clazzName,
                    if (module != null) pathRegistry.getPath(module) else null,
                    definitionResolver,
                    resolutionStack.last()
                )

            val logIndent: String = resolutionStack.indent()
            val logPath = if ("${beanDefinition.path}".isEmpty()) "" else "@ ${beanDefinition.path}"
            val startChar = if (resolutionStack.isEmpty()) "+" else "+"

            Koin.logger.info("$logIndent$startChar-- '$clazzName' $logPath") // @ [$beanDefinition]")
            Koin.logger.debug("$logIndent|-- [$beanDefinition]")

            resolutionStack.resolve(beanDefinition) {
                val (instance, created) = instanceFactory.retrieveInstance<T>(
                    beanDefinition,
                    parameters
                )

                Koin.logger.debug("$logIndent|-- $instance")
                // Log creation
                if (created) {
                    Koin.logger.info("$logIndent\\-- (*)")
                }
                resultInstance = instance
            }
        } catch (e: Exception) {
            resolutionStack.clear()
            Koin.logger.err("Error while resolving instance for class '${clazz.simpleName}' - error: $e ")
            throw e
        }

        return if (resultInstance != null) resultInstance!! else error("Could not create instance for $clazz")
    }

    /**
     * Drop all instances for path context
     * @param path
     */
    fun release(path: String) {
        Koin.logger.info("release module '$path'")

        val paths = pathRegistry.getAllPathsFrom(path)
        val definitions: List<BeanDefinition<*>> =
            beanRegistry.getDefinitions(paths)

        instanceFactory.releaseInstances(definitions)
    }

    /**
     * Close res
     */
    fun close() {
        Koin.logger.debug("[Close] Closing instance resolver")
        resolutionStack.clear()
        instanceFactory.clear()
        beanRegistry.clear()
    }
}