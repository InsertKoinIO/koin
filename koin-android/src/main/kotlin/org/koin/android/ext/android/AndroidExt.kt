package org.koin.android.ext.android

import android.app.Activity
import android.app.Application
import android.app.Service
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

/**
 * Create a new Koin Context
 * @param application - Android application
 * @param modules - vararg of AndroidModule
 */
fun Application.startAndroidContext(application: Application, vararg modules: AndroidModule) {
    StandAloneContext.koinContext = Koin().init(application).build(*modules)
}

/**
 * Return Koin context from Android Context
 */
fun Context.getKoin(): KoinContext = StandAloneContext.koinContext


/* Activity */

/**
 * inject lazily given dependency for Activity
 * @param name - bean name / optional
 */
inline fun <reified T> Activity.inject(name: String = "") = lazy { StandAloneContext.koinContext.get<T>(name) }

/**
 * lazy inject given property for Activity
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Activity.property(key: String) = lazy { StandAloneContext.koinContext.getProperty<T>(key) }

/**
 * lazy inject  given property for Activity
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Activity.property(key: String, defaultValue: T) = lazy { StandAloneContext.koinContext.getProperty(key, defaultValue) }

/* Fragment */

/**
 * lazy inject given dependency for Fragment
 * @param name - bean name / optional
 */
inline fun <reified T> Fragment.inject(name: String = ""): Lazy<T> = lazy { StandAloneContext.koinContext.get<T>(name) }

/**
 * lazy inject given property for Fragment
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Fragment.property(key: String) = lazy { StandAloneContext.koinContext.getProperty<T>(key) }

/**
 * lazy inject given property for Fragment
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Fragment.property(key: String, defaultValue: T) = lazy { StandAloneContext.koinContext.getProperty(key, defaultValue) }


/* Service */

/**
 * lazy inject given dependency for Service
 * @param name - bean name / optional
 */
inline fun <reified T> Service.inject(name: String = ""): Lazy<T> = lazy { StandAloneContext.koinContext.get<T>(name) }

/**
 * lazy inject given property for Service
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> Service.property(key: String) = lazy { StandAloneContext.koinContext.getProperty<T>(key) }

/**
 * lazy inject given property for Service
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> Service.property(key: String, defaultValue: T) = lazy { StandAloneContext.koinContext.getProperty(key, defaultValue) }