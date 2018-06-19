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

import org.koin.core.Koin.Companion.logger
import org.koin.core.bean.BeanRegistry
import org.koin.core.instance.InstanceFactory
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.path.PathRegistry
import org.koin.core.property.PropertyRegistry
import org.koin.core.stack.ResolutionStack
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.MissingPropertyException
import org.koin.standalone.StandAloneKoinContext
import kotlin.reflect.KClass


/**
 * Koin Application ModuleDefinition
 * ModuleDefinition from where you can get beans defined in modules
 *
 * @author - Arnaud GIULIANI
 * @author - Laurent Baresse
 */
class KoinContext(
    val beanRegistry: BeanRegistry,
    val pathRegistry: PathRegistry,
    val propertyResolver: PropertyRegistry,
    val instanceFactory: InstanceFactory
) : StandAloneKoinContext {

    private val resolutionStack = ResolutionStack()

    var contextCallback: ArrayList<ModuleCallback> = arrayListOf()

    /**
     * Retrieve a bean instance
     */
    inline fun <reified T> get(
        name: String = "",
        module: String? = null,
        noinline parameters: ParameterDefinition = emptyParameterDefinition()
    ): T =
        if (name.isEmpty()) resolveByClass(module, parameters) else resolveByName(name, module, parameters)

    /**
     * Resolve a dependency for its bean definition
     * @param name bean definition name
     */
    inline fun <reified T> resolveByName(
        name: String,
        module: String? = null,
        noinline parameters: ParameterDefinition
    ): T =
        resolveInstance(module, T::class, parameters) { beanRegistry.searchByName(name, T::class) }

    /**
     * Resolve a dependency for its bean definition
     * by its inferred type
     */
    inline fun <reified T> resolveByClass(module: String? = null, noinline parameters: ParameterDefinition): T =
        resolveByClass(module, T::class, parameters)

    /**
     * Resolve a dependency for its bean definition
     * byt its type
     */
    inline fun <reified T> resolveByClass(
        module: String? = null,
        clazz: KClass<*>,
        noinline parameters: ParameterDefinition
    ): T =
        resolveInstance(module, clazz, parameters) { beanRegistry.searchAll(clazz) }

    /**
     * Resolve a dependency for its bean definition
     * @param module - module path
     * @param clazz - Class
     * @param parameters - Parameters
     * @param definitionResolver - function to find bean definitions
     */
    fun <T> resolveInstance(
        module: String? = null,
        clazz: KClass<*>,
        parameters: ParameterDefinition,
        definitionResolver: () -> List<BeanDefinition<*>>
    ): T = synchronized(this) {

        val clazzName = clazz.java.canonicalName

        var resultInstance: T? = null

        try {
            val beanDefinition: BeanDefinition<*> =
                beanRegistry.getVisibleBean(
                    clazzName,
                    if (module != null) pathRegistry.getPath(module) else null,
                    definitionResolver,
                    resolutionStack.last()
                )

            val logIndent = resolutionStack.indent()
            resolutionStack.resolve(beanDefinition) {
                // Resolution log
                logger.log("${logIndent}Resolve class[$clazzName] with $beanDefinition")

                val (instance, created) = instanceFactory.retrieveInstance<T>(
                    beanDefinition,
                    parameters
                )

                // Log creation
                if (created) {
                    logger.log("$logIndent(*) Created")
                }

                resultInstance = instance
            }
        } catch (e: Exception) {
            resolutionStack.clear()
            logger.err("Error while resolving instance for class '${clazz.java.simpleName}' - error: $e ")
            throw e
        }

        return if (resultInstance != null) resultInstance!! else error("Could not create instance for $clazz")
    }

    /**
     * Drop all instances for path context
     * @param path
     */
    fun release(path: String) {
        logger.log("Release instances : $path")

        val paths = pathRegistry.getAllPathsFrom(path)
        val definitions: List<BeanDefinition<*>> =
            beanRegistry.getDefinitions(paths)

        instanceFactory.releaseInstances(definitions)

        contextCallback.forEach { it.onRelease(path) }
    }

    /**
     * Retrieve a property by its key
     * can throw MissingPropertyException if the property is not found
     * @param key
     * @throws MissingPropertyException if key is not found
     */
    inline fun <reified T> getProperty(key: String): T = propertyResolver.getProperty(key)

    /**
     * Retrieve a property by its key or return provided default value
     * @param key - property key
     * @param defaultValue - default value if property is not found
     */
    inline fun <reified T> getProperty(key: String, defaultValue: T): T =
        propertyResolver.getProperty(key, defaultValue)

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any) = propertyResolver.add(key, value)

    /**
     * Close res
     */
    fun close() {
        logger.log("[Close] Closing Koin context")
        resolutionStack.clear()
        instanceFactory.clear()
        beanRegistry.clear()
        pathRegistry.clear()
        propertyResolver.clear()
    }
}