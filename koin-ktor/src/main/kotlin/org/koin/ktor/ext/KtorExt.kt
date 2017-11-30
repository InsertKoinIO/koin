package org.koin.ktor.ext

import io.ktor.application.Application
import org.koin.Koin
import org.koin.KoinContext
import org.koin.dsl.module.Module
import org.koin.ktor.bindApplication
import org.koin.standalone.StandAloneContext

/**
 * Start Koin for Ktor Application
 * @param application - Ktor Application
 * @param list - module list
 * @param bindSystemProperties - bind system properties
 * @param properties - additional properties
 */
fun Application.startContext(application: Application, list: List<Module>, bindSystemProperties: Boolean = false, properties: Map<String, Any> = HashMap()) {
    val koin = if (bindSystemProperties) {
        // Koin properties will override system properties
        Koin().bindKoinProperties().bindAdditionalProperties(properties).bindSystemProperties()
    } else {
        Koin().bindKoinProperties().bindAdditionalProperties(properties)
    }

    StandAloneContext.koinContext = koin.bindApplication(application).build(list)
}

/**
 * inject lazily given dependency
 * @param name - bean name / optional
 */
inline fun <reified T> Application.inject(name: String = "") = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name) }

/**
 * lazy inject given property
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Application.property(key: String) = lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject  given property
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Application.property(key: String, defaultValue: T) = lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }


/**
 * Help work on Context
 */
private fun context() = (StandAloneContext.koinContext as KoinContext)

/**
 * Set property value
 *
 * @param key - key property
 * @param value - property value
 *
 */
fun Application.bindProperty(key: String, value: Any) = context().setProperty(key, value)