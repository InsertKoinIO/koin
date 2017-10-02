package org.koin.android.ext.android.app

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.support.v4.app.Fragment
import org.koin.KoinContext
import org.koin.android.KoinContextAware
import org.koin.android.error.KoinApplicationException


/* Application */

/**
 * Return Koin context from Application
 */
@Throws(KoinApplicationException::class)
fun Application.getKoin(): KoinContext =
        if (this is KoinContextAware) getKoin() else throw KoinApplicationException("Your application is not a Koin Application. Please use KoinContextAware interface in your application class.")

/**
 * Return Koin context from Android Context
 */
fun Context.getKoin(): KoinContext = this.applicationContext.getKoin()


/* Activity */

/**
 * Return Koin context from Activity
 */
fun Activity.getKoin(): KoinContext = this.application.getKoin()

/**
 * Inject current dependency
 */
inline fun <reified T> Activity.get(name: String = "") = getKoin().get<T>(name)

/**
 * Release scope instances
 */
fun Activity.release(instance: Any) = getKoin().release(instance)

/**
 * inject lazily given dependency for Activity
 */
inline fun <reified T> Activity.inject(name: String = "") = lazy { getKoin().get<T>(name) }

/**
 * inject lazily given property for Activity
 */
inline fun <reified T> Activity.property(key: String) = lazy { getKoin().getProperty<T>(key) }

/* Fragment */

/**
 * Return Koin context from Fragment
 */
fun Fragment.getKoin(): KoinContext = activity.getKoin()

/**
 * Inject current dependency
 */
inline fun <reified T> Fragment.get(name: String = ""): T = getKoin().get(name)

/**
 * inject lazily given dependency for Fragment
 */
inline fun <reified T> Fragment.inject(name: String = ""): Lazy<T> = lazy { getKoin().get<T>(name) }

/**
 * Release scope instances
 */
fun Fragment.release(instance: Any) = getKoin().release(instance)

/**
 * inject lazily given property for Fragment
 */
inline fun <reified T> Fragment.property(key: String) = lazy { getKoin().getProperty<T>(key) }

/* Service */

/**
 * Service Koin Context
 */
fun Service.getKoin() = this.application.getKoin()

/**
 * Inject current dependency
 */
inline fun <reified T> Service.get(name: String = ""): T = getKoin().get(name)

/**
 * inject lazily given dependency for Activity
 */
inline fun <reified T> Service.inject(name: String = ""): Lazy<T> = lazy { getKoin().get<T>(name) }

/**
 * inject lazily given property for Service
 */
inline fun <reified T> Service.property(key: String) = lazy { getKoin().getProperty<T>(key) }

/**
 * Release scope instances
 */
fun Service.release(instance: Any) = getKoin().release(instance)