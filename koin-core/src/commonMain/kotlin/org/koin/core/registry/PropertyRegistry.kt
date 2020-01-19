package org.koin.core.registry

import org.koin.core.Koin

expect class PropertyRegistry(_koin: Koin) {
    val _koin: Koin

    /**
     * saveProperty all properties to registry
     * @param properties
     */
    fun saveProperties(properties: Map<String, Any>)

    /**
     * Load properties from Property file
     * @param fileName
     */
    fun loadPropertiesFromFile(fileName: String)

    /**
     * Load properties from environment
     */
    fun loadEnvironmentProperties()

    /**
     * Get a property
     * @param key
     */
    fun <T> getProperty(key: String): T?

    /**
     * save a property (key,value)
     */
    internal fun <T : Any> saveProperty(key: String, value: T)

    /**
     * Delete a property (key,value)
     */
    fun deleteProperty(key: String)
    fun close()
}
