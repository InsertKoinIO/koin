package org.koin.core.registry

import org.koin.core.Koin
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.error.NoPropertyFileFoundException
import org.koin.ext.isFloat
import org.koin.ext.isInt
import java.util.*

@Suppress("UNCHECKED_CAST")
class PropertyRegistry {

    private val values = hashMapOf<String, Any>()

    fun addAll(v: Map<String, Any>) {
        values.putAll(v)
    }

    fun add(key: String, value: Any) {
        values[key] = value
    }


    fun <T> getProperty(key: String): T? {
        return values[key] as? T?
    }

    fun <T> setProperty(key: String, value: T) {
        values[key] = value as Any
    }

    fun loadPropertiesFromFile(fileName: String) {
        val content = Koin::class.java.getResource(fileName)?.readText()
        if (content != null) {
            logger.info("loaded properties from file:'$fileName'")
            val properties = readDataFromFile(content)
            properties.registerStringValues()
        } else {
            throw NoPropertyFileFoundException("No properties found for file '$fileName'")
        }
    }

    private fun Properties.registerStringValues() {
        val propertiesMapValues = this.toMap() as Map<String, String>
        propertiesMapValues.forEach { (k: String, v: String) ->
            when {
                v.isInt() -> add(k, v.toInt())
                v.isFloat() -> add(k, v.toFloat())
                else -> add(k, v)
            }
        }
    }

    private fun readDataFromFile(content: String): Properties {
        val properties = Properties()
        properties.load(content.byteInputStream())
        return properties
    }

    fun loadEnvironmentProperties() {
        val sysProperties = System.getProperties()
        sysProperties.registerStringValues()

        val sysEnvProperties = System.getenv().toProperties()
        sysEnvProperties.registerStringValues()
    }
}
