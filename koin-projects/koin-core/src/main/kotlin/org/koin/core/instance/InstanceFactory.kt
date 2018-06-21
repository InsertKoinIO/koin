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

import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.error.BeanInstanceCreationException


/**
 * Instance factory - handle objects creation against BeanRegistry
 * @author - Arnaud GIULIANI
 */
open class InstanceFactory {

    val instances = HashMap<BeanDefinition<*>, Any>()

    /**
     * Retrieve or create bean instance
     * @return Instance / has been created
     */
    fun <T> retrieveInstance(def: BeanDefinition<*>, p: ParameterDefinition): Pair<T, Boolean> {
        // Factory
        return if (def.isNotASingleton()) {
            Pair(createInstance(def, p), true)
        } else {
            // Singleton
            val found: T? = findInstance<T>(def)
            val instance: T = found ?: createInstance(def, p)
            val created = found == null
            if (created) {
                saveInstance(def, instance)
            }
            Pair(instance, created)
        }
    }

    private fun <T> saveInstance(def: BeanDefinition<*>, instance: T) {
        instances[def] = instance as Any
    }

    /**
     * Find existing instance
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> findInstance(def: BeanDefinition<*>): T? {
        val existingClass = instances.keys.firstOrNull { it == def }
        return if (existingClass != null) {
            instances[existingClass] as? T
        } else {
            null
        }
    }

    /**
     * create instance for given bean definition
     */
    @Suppress("UNCHECKED_CAST")
    open fun <T> createInstance(def: BeanDefinition<*>, p: ParameterDefinition): T {
        try {
            val parameterList = p()
            val instance = def.definition.invoke(parameterList) as Any
            instance as T
            return instance
        } catch (e: Throwable) {
            val stack = e.stackTrace.takeWhile { !it.className.contains("sun.reflect") }
                .joinToString("\n\t\t")
            throw BeanInstanceCreationException("Can't create definition '$def' due to error :\n\t\t${e.message}\n\t\t$stack")
        }
    }

    /**
     * Drop all instances for definitions
     */
    fun releaseInstances(definitions: List<BeanDefinition<*>>) {
        definitions.forEach { instances.remove(it) }
    }

    /**
     * Clear all resources
     */
    fun clear() {
        instances.clear()
    }

}