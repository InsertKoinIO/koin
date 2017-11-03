package org.koin.core.property

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
     * Set a property
     */
    fun setProperty(key: String, value: Any) {
        properties[key] = value
    }

    /**
     * Add properties
     */
    fun addAll(props: Map<String, Any>) {
        properties += props
    }

    /**
     * Delete a property
     */
    fun delete(key: String) {
        properties.remove(key)
    }
}