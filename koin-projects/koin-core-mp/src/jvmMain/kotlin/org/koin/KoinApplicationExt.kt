package org.koin

import org.koin.core.KoinApplication
import org.koin.core.registry.loadEnvironmentProperties
import org.koin.core.registry.loadPropertiesFromFile


/**
 * Load properties from file
 * @param fileName
 */
fun KoinApplication.fileProperties(fileName: String = "/koin.properties"): KoinApplication {
    koin._propertyRegistry.loadPropertiesFromFile(fileName)
    return this
}

/**
 * Load properties from environment
 */
fun KoinApplication.environmentProperties(): KoinApplication {
    koin._propertyRegistry.loadEnvironmentProperties()
    return this
}