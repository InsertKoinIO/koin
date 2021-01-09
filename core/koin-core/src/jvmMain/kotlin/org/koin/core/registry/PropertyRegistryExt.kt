package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.error.NoPropertyFileFoundException
import org.koin.core.logger.Level
import org.koin.ext.quoted
import java.util.*


/**
 *Save properties values into PropertyRegister
 */
fun PropertyRegistry.saveProperties(properties: Properties) {
    if (_koin.logger.isAt(Level.DEBUG)) {
        _koin.logger.debug("load ${properties.size} properties")
    }

    val propertiesMapValues = properties.toMap() as Map<String, String>
    propertiesMapValues.forEach { (k: String, v: String) ->
        saveProperty(k, v.quoted())
    }
}

/**
 * Load properties from Property file
 * @param fileName
 */
fun PropertyRegistry.loadPropertiesFromFile(fileName: String) {
    if (_koin.logger.isAt(Level.DEBUG)) {
        _koin.logger.debug("load properties from $fileName")
    }
    val content = Koin::class.java.getResource(fileName)?.readText()
    if (content != null) {
        if (_koin.logger.isAt(Level.INFO)) {
            _koin.logger.info("loaded properties from file:'$fileName'")
        }
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
    if (_koin.logger.isAt(Level.DEBUG)) {
        _koin.logger.debug("load properties from environment")
    }
    val sysProperties = System.getProperties()
    saveProperties(sysProperties)

    val sysEnvProperties = System.getenv().toProperties()
    saveProperties(sysEnvProperties)
}