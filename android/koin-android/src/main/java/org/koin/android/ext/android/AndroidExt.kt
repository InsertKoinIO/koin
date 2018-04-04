package org.koin.android.ext.android

import android.app.Application
import android.content.ComponentCallbacks
import org.koin.Koin
import org.koin.KoinContext
import org.koin.android.ext.koin.bindAndroidProperties
import org.koin.android.ext.koin.with
import org.koin.android.logger.AndroidLogger
import org.koin.core.parameter.Parameters
import org.koin.dsl.module.Module
import org.koin.log.Logger
import org.koin.standalone.StandAloneContext


/**
 * Help work on context
 */
private fun context() = (StandAloneContext.koinContext as KoinContext)

/**
 * Create a new Koin Context
 * @param application - Android application
 * @param modules - list of AndroidModule
 *
 * will be soon deprecated for starKoin() with <application>
 */
fun Application.startKoin(
    application: Application,
    modules: List<Module>,
    properties: Map<String, Any> = HashMap(),
    logger: Logger = AndroidLogger()
) {
    Koin.logger = logger
    StandAloneContext.startKoin(modules, properties = properties)
        .with(application)
        .bindAndroidProperties(application)
}

/**
 * Create a new Koin Context
 * @param modules - list of AndroidModule
 *
 * will be soon deprecated for starKoin() with <application>
 */
fun Application.startKoin(
    modules: List<Module>,
    properties: Map<String, Any> = HashMap(),
    logger: Logger = AndroidLogger()
) {
    Koin.logger = logger
    StandAloneContext.startKoin(modules, properties = properties)
        .with(this)
        .bindAndroidProperties(this)
}

/**
 * Bind an Android String to Koin property
 * @param id - Android resource String id
 * @param key - Koin property key
 */
fun Application.bindString(id: Int, key: String) {
    context().setProperty(key, context().get<Application>().getString(id))
}

/**
 * Bind an Android Integer to Koin property
 * @param id - Android resource Int id
 * @param key - Koin property key
 */
fun Application.bindInt(id: Int, key: String) {
    context().setProperty(key, context().get<Application>().resources.getInteger(id))
}

/**
 * Bind an Android Boolean to Koin property
 * @param id - Android resource Boolean id
 * @param key - Koin property key
 */
fun Application.bindBool(id: Int, key: String) {
    context().setProperty(key, context().get<Application>().resources.getBoolean(id))
}

/**
 * inject lazily given dependency for Android component
 * @param name - bean name / optional
 */
inline fun <reified T> ComponentCallbacks.inject(
    name: String = ""
): Lazy<T> = lazy { get<T>(name, { emptyMap() }) }

/**
 * inject lazily given dependency for Android component
 * @param name - bean name / optional
 * @param parameters - dynamic parameters
 */
inline fun <reified T> ComponentCallbacks.inject(
    name: String = "",
    noinline parameters: Parameters
): Lazy<T> = lazy { get<T>(name, parameters) }

/**
 * get given dependency for Android component
 * @param name - bean name / optional
 */
inline fun <reified T> ComponentCallbacks.get(
    name: String = ""
): T = (StandAloneContext.koinContext as KoinContext).get(name, { emptyMap() })

/**
 * get given dependency for Android component
 * @param name - bean name
 * @param parameters - dynamic parameters
 */
inline fun <reified T> ComponentCallbacks.get(
    name: String = "",
    noinline parameters: Parameters
): T = (StandAloneContext.koinContext as KoinContext).get(name, parameters)

/**
 * lazy inject given property for Android component
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> ComponentCallbacks.property(key: String): Lazy<T> =
    lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject  given property for Android component
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> ComponentCallbacks.property(key: String, defaultValue: T): Lazy<T> =
    lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }

/**
 * Set a property
 *
 * @param key
 * @param value
 */
fun ComponentCallbacks.setProperty(key: String, value: Any): Unit =
    context().setProperty(key, value)

/**
 * Release a context
 * @param name
 */
fun ComponentCallbacks.releaseContext(name: String): Unit = context().releaseContext(name)

/**
 * Release properties
 * @param keys - property keys
 */
fun ComponentCallbacks.releaseProperties(vararg keys: String): Unit =
    context().releaseProperties(*keys)
