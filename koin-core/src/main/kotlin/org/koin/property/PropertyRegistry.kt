package org.koin.property

/**
 * Gather all properties
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
class PropertyRegistry {
    val properties = HashMap<String, Any?>()

    fun <T> get(key: String): T = properties[key] as T

    fun set(key: String, value: Any?) {
        properties[key] = value
    }
}