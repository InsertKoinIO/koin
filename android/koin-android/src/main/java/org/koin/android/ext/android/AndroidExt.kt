package org.koin.android.ext.android

import android.app.Application
import android.content.ComponentCallbacks
import org.koin.Koin
import org.koin.KoinContext
import org.koin.android.ext.koin.bindAndroidProperties
import org.koin.android.ext.koin.init
import org.koin.android.module.AndroidModule
import org.koin.standalone.StandAloneContext


internal fun context() = (StandAloneContext.koinContext as KoinContext)

/**
 * Create a new Koin Context
 * @param application - Android application
 * @param modules - list of AndroidModule
 */
fun Application.startAndroidContext(application: Application, modules: List<AndroidModule>, properties: Map<String, Any> = HashMap()) {
    StandAloneContext.koinContext = Koin().bindAndroidProperties(application).bindAdditionalProperties(properties).init(application).build(modules)
}

/**
 * Bind an Android String to Koin property
 * @param id - Android resource String id
 * @param key - Koin property key
 */
fun ComponentCallbacks.bindString(id: Int, key: String) {
    context().setProperty(key, context().get<Application>().getString(id))
}

/**
 * Bind an Android Integer to Koin property
 * @param id - Android resource Int id
 * @param key - Koin property key
 */
fun ComponentCallbacks.bindInt(id: Int, key: String) {
    context().setProperty(key, context().get<Application>().resources.getInteger(id))
}

/**
 * Bind an Android Boolean to Koin property
 * @param id - Android resource Boolean id
 * @param key - Koin property key
 */
fun ComponentCallbacks.bindBool(id: Int, key: String) {
    context().setProperty(key, context().get<Application>().resources.getBoolean(id))
}

/**
 * inject lazily given dependency for Activity
 * @param name - bean name / optional
 */
inline fun <reified T> ComponentCallbacks.inject(name: String = "") = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name) }

/**
 * lazy inject given property for Activity
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> ComponentCallbacks.property(key: String) = lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject  given property for Activity
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> ComponentCallbacks.property(key: String, defaultValue: T) = lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }

/**
 * Set a property
 * @param key
 * @param value
 */
fun ComponentCallbacks.bindProperty(key: String, value: Any) = context().propertyResolver.add(key, value)

/**
 * Release a context
 * @param name
 */
fun ComponentCallbacks.releaseContext(name: String) = context().releaseContext(name)

/**
 * Release properties
 * @param keys - property keys
 */
fun ComponentCallbacks.releaseProperties(vararg keys: String) = context().releaseProperties(*keys)
