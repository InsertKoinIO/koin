package org.koin.core.property

import org.koin.error.MissingPropertyException

/**
 * Resolve properties for a context
 * @author - Arnaud GIULIANI
 */
class PropertyRegistry {

    val properties = HashMap<String, Any>()

    inline fun <reified T> get(key: String): T = if (key in properties.keys) properties[key] as? T?
            ?: throw MissingPropertyException("Can't cast property with key '$key' to ${T::class}")
    else throw MissingPropertyException("can't find property with key '$key'")

    inline fun <reified T> getOrElse(key: String, defaultValue: T): T {
        return try { get(key) } catch (e: MissingPropertyException) { defaultValue }
    }

    fun set(key: String, value: Any) {
        properties[key] = value
    }

    /**
     * Set a property
     */
    fun setProperty(key: String, value: Any) = set(key, value)

    /**
     * Retrieve a property or get MissingPropertyException thrown
     */
    inline fun <reified T> getProperty(key: String): T = get(key)

    /**
     * Retrieve a property or get provided default value
     */
    inline fun <reified T> getPropertyOrElse(key: String, defaultValue: T): T = getOrElse(key, defaultValue)

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