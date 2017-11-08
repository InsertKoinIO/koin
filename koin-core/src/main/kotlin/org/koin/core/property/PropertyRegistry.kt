package org.koin.core.property

import org.koin.Koin
import org.koin.error.MissingPropertyException

/**
 * Resolve properties for a context
 * @author - Arnaud GIULIANI
 */
class PropertyRegistry {

    val properties = HashMap<String, Any>()

    /**
     * Get value for given property
     * @param key - key property
     * @throws MissingPropertyException if property is missing
     */
    inline fun <reified T> getProperty(key: String): T = properties[key] as T? ?: throw MissingPropertyException("Can't find property '$key'")

    /**
     * Get value for given property or get default value if property key is missing
     * @param key - key property
     * @param defaultValue - default value for key
     */
    inline fun <reified T> getProperty(key: String, defaultValue: T): T {
        return properties[key] as T? ?: defaultValue
    }

    /**
     * Add properties
     */
    fun addAll(props: Map<String, Any>) {
        Koin.logger.log("addAll properties ${props.keys}")
        properties += props
    }

    /**
     * Add property
     */
    fun add(key: String, value: Any) {
        Koin.logger.log("add property $key")
        properties += Pair(key, value)
    }

    /**
     * Delete a property
     */
    fun delete(key: String) {
        Koin.logger.log("delete property $key")
        properties.remove(key)
    }

    /**
     * Delete properties
     */
    fun deleteAll(keys: Array<out String>) {
        keys.forEach { delete(it) }
    }
}