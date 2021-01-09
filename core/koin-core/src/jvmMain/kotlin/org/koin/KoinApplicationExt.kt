package org.koin

import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternal
import org.koin.core.registry.loadEnvironmentProperties
import org.koin.core.registry.loadPropertiesFromFile


/**
 * Load properties from file
 * @param fileName
 */
@OptIn(KoinInternal::class)
fun KoinApplication.fileProperties(fileName: String = "/koin.properties"): KoinApplication {
    koin.propertyRegistry.loadPropertiesFromFile(fileName)
    return this
}

/**
 * Load properties from environment
 */
@OptIn(KoinInternal::class)
fun KoinApplication.environmentProperties(): KoinApplication {
    koin.propertyRegistry.loadEnvironmentProperties()
    return this
}