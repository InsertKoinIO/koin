package org.koin.android.ext.android.app

import android.app.Activity
import android.app.Application
import android.app.Service
import android.support.v4.app.Fragment
import org.koin.KoinContext
import org.koin.android.KoinContextAware
import org.koin.android.error.KoinApplicationException
import kotlin.reflect.KClass


/* Application */

/**
 * Return Koin context from Application
 */
@Throws(KoinApplicationException::class)
fun Application.getKoin(): KoinContext {
    if (this is KoinContextAware) {
        return getKoin()
    } else throw KoinApplicationException("Your application is not a Koin Application. Please use KoinContextAware interface in your application class.")
}

/* Activity */

/**
 * Return Koin context from Activity
 */
fun Activity.getKoin(): KoinContext = this.application.getKoin()

/**
 * Inject current dependency
 */
inline fun <reified T> Activity.get(): T = getKoin().get<T>()

/**
 * Release scope instances
 */
fun Activity.release(vararg scopeClasses: KClass<*>) = getKoin().release(*scopeClasses)

/**
 * inject lazily given dependency for Activity
 */
inline fun <reified T> Activity.inject(): Lazy<T> = lazy { getKoin().get<T>() }

/**
 * inject lazily given dependency for Activity - can be null
 */
inline fun <reified T> Activity.injectOrNull(): Lazy<T?> = lazy { getKoin().getOrNull<T>() }


/* Fragment */

/**
 * Return Koin context from Fragment
 */
fun Fragment.getKoin(): KoinContext = activity.getKoin()

/**
 * Inject current dependency
 */
inline fun <reified T> Fragment.get(): T = getKoin().get()

/**
 * inject lazily given dependency for Fragment
 */
inline fun <reified T> Fragment.inject(): Lazy<T> = lazy { getKoin().get<T>() }

/**
 * inject lazily given dependency for Fragment - can be null
 */
inline fun <reified T> Fragment.injectOrNull(): Lazy<T?> = lazy { getKoin().getOrNull<T>() }

/**
 * Release scope instances
 */
fun Fragment.release(vararg scopeClasses: KClass<*>) = getKoin().release(*scopeClasses)


/* Service */

/**
 * Service Koin Context
 */
fun Service.getKoin() = this.application.getKoin()

/**
 * Inject current dependency
 */
inline fun <reified T> Service.get(): T = getKoin().get<T>()

/**
 * inject lazily given dependency for Activity
 */
inline fun <reified T> Service.inject(): Lazy<T> = lazy { getKoin().get<T>() }

/**
 * inject lazily given dependency for Activity - can be null
 */
inline fun <reified T> Service.injectOrNull(): Lazy<T?> = lazy { getKoin().getOrNull<T>() }

/**
 * Release scope instances
 */
fun Service.release(vararg scopeClasses: KClass<*>) = getKoin().release(*scopeClasses)