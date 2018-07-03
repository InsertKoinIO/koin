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
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.property.PropertyRegistry
import org.koin.error.MissingPropertyException
import org.koin.standalone.StandAloneKoinContext


/**
 * Koin Application ModuleDefinition
 * ModuleDefinition from where you can get beans defined in modules
 *
 * @author - Arnaud GIULIANI
 * @author - Laurent Baresse
 */
class KoinContext(
    val instanceResolver: InstanceResolver,
    val propertyResolver: PropertyRegistry
) : StandAloneKoinContext {

    val contextCallback: ArrayList<ModuleCallback> = arrayListOf()

    /**
     * Retrieve an instance from its name/class
     */
    inline fun <reified T> get(
        name: String = "",
        module: String? = null,
        noinline parameters: ParameterDefinition = emptyParameterDefinition()
    ): T = instanceResolver.resolve(
        InstanceRequest(
            name = name,
            module = module,
            clazz = T::class,
            parameters = parameters
        )
    )

    /**
     * Retrieve an instance from its class
     */
    fun <T> get(request: ClassRequest): T = instanceResolver.resolve(request)

    /**
     * Retrieve an instance from a given filter
     */
    fun <T> get(request: CustomRequest): T = instanceResolver.proceedResolution(
        request.module,
        request.clazz,
        request.parameters
    ) { request.defininitionFilter(instanceResolver.beanRegistry) }

    /**
     * Drop all instances for path context
     * @param path
     */
    fun release(path: String) {
        logger.info("release module '$path'")

        instanceResolver.release(path)
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
        logger.info("[Close] Closing Koin context")
        instanceResolver.close()
        propertyResolver.clear()
    }
}