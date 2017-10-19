package org.koin.android.ext.android

import android.app.Activity
import android.app.Service
import android.content.Context
import android.support.v4.app.Fragment
import org.koin.KoinContext
import org.koin.standalone.StandAloneContext


/**
 * Return Koin context from Android Context
 */
fun Context.getKoin(): KoinContext = StandAloneContext.koinContext


/* Activity */

/**
 * inject lazily given dependency for Activity
 */
inline fun <reified T> Activity.inject(name: String = "") = lazy { StandAloneContext.koinContext.get<T>(name) }

/**
 * inject lazily given property for Activity
 */
inline fun <reified T> Activity.property(key: String) = lazy { StandAloneContext.koinContext.getProperty<T>(key) }

/* Fragment */

/**
 * inject lazily given dependency for Fragment
 */
inline fun <reified T> Fragment.inject(name: String = ""): Lazy<T> = lazy { StandAloneContext.koinContext.get<T>(name) }

/**
 * inject lazily given property for Fragment
 */
inline fun <reified T> Fragment.property(key: String) = lazy { StandAloneContext.koinContext.getProperty<T>(key) }

/* Service */

/**
 * inject lazily given dependency for Activity
 */
inline fun <reified T> Service.inject(name: String = ""): Lazy<T> = lazy { getKoin().get<T>(name) }

/**
 * inject lazily given property for Service
 */
inline fun <reified T> Service.property(key: String) = lazy { getKoin().getProperty<T>(key) }
