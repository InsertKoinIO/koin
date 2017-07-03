package org.koin.property

import org.koin.error.MissingPropertyException

/**
 * Resolve properties for a context
 * @author - Arnaud GIULIANI
 */
class PropertyResolver {

    val registry = PropertyRegistry()

    /**
     * Retrieve a property
     */
    fun <T> getProperty(key: String): T = registry.get(key) ?: throw MissingPropertyException("Could not bind property $key")

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any) = registry.set(key, value)

    /**
     * Retrieve a property or null
     */
    fun <T> getPropertyOrNull(key: String): T? = registry.get(key)

    fun addAll(props: Map<String, Any>) {
        registry.properties += props
    }
}