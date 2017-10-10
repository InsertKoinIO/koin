package org.koin.core.property

/**
 * Resolve properties for a context
 * @author - Arnaud GIULIANI
 */
class PropertyRegistry {

    val properties = HashMap<String, Any?>()

    fun <T> get(key: String): T = properties[key] as T

    fun set(key: String, value: Any?) {
        properties[key] = value
    }

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any?) = set(key, value)

    /**
     * Retrieve a property or null
     */
    fun <T> getProperty(key: String): T = get(key)

    fun addAll(props: Map<String, Any>) {
        properties += props
    }
}