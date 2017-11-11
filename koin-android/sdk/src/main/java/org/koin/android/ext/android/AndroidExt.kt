package org.koin.android.ext.android

import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment
import org.koin.Koin
import org.koin.KoinContext
import org.koin.android.ext.koin.init
import org.koin.android.module.AndroidModule
import org.koin.standalone.StandAloneContext


/**
 * Create a new Koin Context
 * @param application - Android application
 * @param modules - list of AndroidModule
 */
fun Application.startAndroidContext(application: Application, modules: List<AndroidModule>) {
    StandAloneContext.koinContext = Koin().init(application).build(modules)
}

private val koinContext = (StandAloneContext.koinContext as KoinContext)


/**
 * Bind an Android String to Koin property
 * @param id - Android resource String id
 * @param key - Koin property key
 */
fun Context.bindString(id: Int, key: String) {
    koinContext.setProperty(key, koinContext.get<Application>().getString(id))
}

/**
 * Bind an Android Integer to Koin property
 * @param id - Android resource Int id
 * @param key - Koin property key
 */
fun Context.bindInt(id: Int, key: String) {
    koinContext.setProperty(key, koinContext.get<Application>().resources.getInteger(id))
}

/**
 * Bind an Android Boolean to Koin property
 * @param id - Android resource Boolean id
 * @param key - Koin property key
 */
fun Context.bindBool(id: Int, key: String) {
    koinContext.setProperty(key, koinContext.get<Application>().resources.getBoolean(id))
}

/* Activity */

/**
 * inject lazily given dependency for Activity
 * @param name - bean name / optional
 */
inline fun <reified T> Context.inject(name: String = "") = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name) }

/**
 * lazy inject given property for Activity
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Context.property(key: String) = lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject  given property for Activity
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Context.property(key: String, defaultValue: T) = lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }


fun Context.bindProperty(key: String, value: Any) = lazy { koinContext.propertyResolver.add(key, value) }

fun Context.releaseContext(name: String) = koinContext.releaseContext(name)

fun Context.releaseProperties(vararg keys: String) = koinContext.releaseProperties(*keys)


/* Support Fragment */

/**
 * lazy inject given dependency for Fragment
 * @param name - bean name / optional
 */
inline fun <reified T> Fragment.inject(name: String = ""): Lazy<T> = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name) }

/**
 * lazy inject given property for Fragment
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Fragment.property(key: String) = lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject given property for Fragment
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Fragment.property(key: String, defaultValue: T) = lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }

fun Fragment.bindProperty(key: String, value: Any) = lazy { koinContext.propertyResolver.add(key, value) }

fun Fragment.releaseContext(name: String) = koinContext.releaseContext(name)

fun Fragment.releaseProperties(vararg keys: String) = koinContext.releaseProperties(*keys)