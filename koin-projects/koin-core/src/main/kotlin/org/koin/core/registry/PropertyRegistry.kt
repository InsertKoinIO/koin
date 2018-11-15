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
package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.error.NoPropertyFileFoundException
import org.koin.ext.isFloat
import org.koin.ext.isInt
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Property Registry
 * Save/find all Koin properties
 *
 * @author Arnaud Giuliani
 */
@Suppress("UNCHECKED_CAST")
class PropertyRegistry {

    private val values: MutableMap<String, Any> = ConcurrentHashMap()

    /**
     * saveProperty all properties to registry
     * @param properties
     */
    fun saveProperties(properties: Map<String, Any>) {
        logger.debug("load ${properties.size} properties")

        values.putAll(properties)
    }

    /**
     *Save properties values into PropertyRegister
     */
    fun saveProperties(properties: Properties) {
        logger.debug("load ${properties.size} properties")

        val propertiesMapValues = properties.toMap() as Map<String, String>
        propertiesMapValues.forEach { (k: String, v: String) ->
            when {
                v.isInt() -> saveProperty(k, v.toInt())
                v.isFloat() -> saveProperty(k, v.toFloat())
                else -> saveProperty(k, v)
            }
        }
    }

    /**
     * save a property (key,value)
     */
    internal fun <T : Any> saveProperty(key: String, value: T) {
        values[key] = value
    }

    /**
     * Get a property
     * @param key
     */
    fun <T> getProperty(key: String): T? {
        return values[key] as? T?
    }

    /**
     * Load properties from Property file
     * @param fileName
     */
    fun loadPropertiesFromFile(fileName: String) {
        logger.debug("load properties from $fileName")

        val content = Koin::class.java.getResource(fileName)?.readText()
        if (content != null) {
            logger.info("loaded properties from file:'$fileName'")
            val properties = readDataFromFile(content)
            saveProperties(properties)
        } else {
            throw NoPropertyFileFoundException("No properties found for file '$fileName'")
        }
    }

    private fun readDataFromFile(content: String): Properties {
        val properties = Properties()
        properties.load(content.byteInputStream())
        return properties
    }

    /**
     * Load properties from environment
     */
    fun loadEnvironmentProperties() {
        logger.debug("load properties from environment")

        val sysProperties = System.getProperties()
        saveProperties(sysProperties)

        val sysEnvProperties = System.getenv().toProperties()
        saveProperties(sysEnvProperties)
    }

    fun close() {
        values.clear()
    }
}
