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

    properties.filter { (key, _) ->
        if (key is String) {
            return@filter true
        }
        else {
            _koin.logger.debug("Skipping property $key")
            return@filter false
        }
    }.forEach { (key, value) ->
        saveProperty(key as String, value)
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
