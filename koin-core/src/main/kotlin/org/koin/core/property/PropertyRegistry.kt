package org.koin.core.property

import org.koin.error.MissingPropertyException

/**
 * Resolve properties for a context
 * @author - Arnaud GIULIANI
 */
class PropertyRegistry {

    val properties = HashMap<String, Any?>()

    inline fun <reified T> get(key: String): T = if (key in properties.keys) properties[key] as? T?
            ?: throw MissingPropertyException("Can't cast property with key '$key' to ${T::class}")
    else throw MissingPropertyException("can't find property with key '$key'")

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
    inline fun <reified T> getProperty(key: String): T = get(key)

    fun addAll(props: Map<String, Any>) {
        properties += props
    }
}