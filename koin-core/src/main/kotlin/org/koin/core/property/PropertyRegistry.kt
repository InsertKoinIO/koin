package org.koin.core.property

import org.koin.Koin
import org.koin.error.MissingPropertyException
import java.util.*

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
        val value = properties[key] as T? ?: defaultValue
        Koin.logger.log("[Property] get $key << '$value'")
        return value
    }

    /**
     * Add properties
     */
    fun addAll(props: Map<String, Any>) {
        Koin.logger.log("[Property] add properties ${props.size}")
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
        Koin.logger.log("[Property] $key >> '$value'")
        properties += Pair(key, value)
    }

    /**
     * Delete a property
     */
    fun delete(key: String) {
        Koin.logger.log("[Property] delete $key")
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