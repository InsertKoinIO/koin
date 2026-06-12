@file:OptIn(KoinInternalApi::class)

package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.error.NoPropertyFileFoundException
import java.util.*

/**
 *Save properties values into PropertyRegister
 */
fun PropertyRegistry.saveProperties(properties: Properties) {
    _koin.logger.debug("load ${properties.size} properties")

    // java.util.Properties can legally hold non-String keys/values (e.g. after
    // System.setProperties(...) with arbitrary objects). The registry is keyed by
    // String but stores Any values, so keep any String-keyed entry (value as-is)
    // and only drop non-String keys — instead of hard-casting values to String and
    // crashing (#2348).
    properties.forEach { (k, v) ->
        if (k is String && v != null) {
            saveProperty(k, v)
        } else {
            _koin.logger.debug("ignore property with non-string key '$k'")
        }
    }
}

/**
 * Load properties from Property file
 * @param fileName
 */
fun PropertyRegistry.loadPropertiesFromFile(fileName: String) {
    _koin.logger.debug("load properties from $fileName")

    val content = Koin::class.java.getResource(fileName)?.readText()
    if (content != null) {
        _koin.logger.info("loaded properties from file:'$fileName'")
        val properties = readDataFromFile(content)
        saveProperties(properties)
    } else {
        throw NoPropertyFileFoundException("No properties found for file '$fileName'")
    }
}

private fun readDataFromFile(content: String): Properties {
    val properties = Properties()
    properties.load(content.byteInputStream())
    return properties
}

/**
 * Load properties from environment
 */
fun PropertyRegistry.loadEnvironmentProperties() {
    _koin.logger.debug("load properties from environment")

    val sysProperties = System.getProperties()
    saveProperties(sysProperties)

    val sysEnvProperties = System.getenv().toProperties()
    saveProperties(sysEnvProperties)
}
