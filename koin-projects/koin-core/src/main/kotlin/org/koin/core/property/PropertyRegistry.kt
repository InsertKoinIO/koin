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
package org.koin.core.property

import org.koin.error.MissingPropertyException
import java.util.*


/**
 * Resolve properties for a context
 * @author - Arnaud GIULIANI
 * @author - Laurent Baresse
 */
class PropertyRegistry {

    val properties = HashMap<String, Any>()

    /**
     * Get value for given property
     * @param key - key property
     * @throws MissingPropertyException if property is missing
     */
    fun <T> getProperty(key: String): T =
        getValue<T>(key) ?: throw MissingPropertyException("Can't find property '$key'")

    /**
     * Retrieve value or null
     * @param key
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(key: String): T? {
        //TODO check for properties parsing from files :/
//        val clazzName = T::class.java.simpleName
//        val value = properties[key]
//        return if (value is String && clazzName != "String") {
//            when (clazzName) {
//                "Integer" -> value.toIntOrNull()
//                "Float" -> value.toFloatOrNull()
//                else -> value
//            } as T?
//        } else value as? T?
        return properties[key] as? T
    }

    /**
     * Get value for given property or get default value if property key is missing
     * @param key - key property
     * @param defaultValue - default value for key
     */
    fun <T> getProperty(key: String, defaultValue: T): T {
        return getValue(key) ?: defaultValue
    }

    /**
     * Add properties
     */
    fun addAll(props: Map<String, Any>) {
        properties += props
    }


    /**
     * Inject all properties to context
     */
    fun import(properties: Properties): Int {
        return properties.keys
            .filter { it is String && properties[it] != null }
            .map { add(it as String, properties[it]!!) }
            .count()
    }

    /**
     * Returns true if property with the given key exists
     */
    fun containsKey(key: String) = properties.containsKey(key)

    /**
     * Add property
     */
    fun add(key: String, value: Any) {
        properties += Pair(key, value)
    }

    /**
     * Delete a property
     */
    fun delete(key: String) {
        properties.remove(key)
    }

    /**
     * Delete properties
     */
    fun deleteAll(keys: Array<out String>) {
        keys.forEach { delete(it) }
    }

    /**
     * Clear all resources
     */
    fun clear() {
        properties.clear()
    }
}