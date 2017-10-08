package org.koin.core.property

/**
 * Resolve properties for a context
 * @author - Arnaud GIULIANI
 */
class PropertyResolver {

    val registry = PropertyRegistry()

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any?) = registry.set(key, value)

    /**
     * Retrieve a property or null
     */
    fun <T> getProperty(key: String): T = registry.get(key)

    fun addAll(props: Map<String, Any>) {
        registry.properties += props
    }
}