package android.support

import android.app.getKoin
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.KoinContext
import kotlin.reflect.KClass

/**
 * Created by arnaud on 05/07/2017.
 */

/**
 * Return Koin context from Activity
 */
fun FragmentActivity.getKoin(): KoinContext = this.application.getKoin()

/**
 * Inject current dependency
 */
inline fun <reified T> FragmentActivity.get(): T = getKoin().get<T>()

/**
 * Release scope instances
 */
fun FragmentActivity.release(vararg scopeClasses: KClass<*>) = getKoin().release(*scopeClasses)

/**
 * inject lazily given dependency for Activity
 */
inline fun <reified T> FragmentActivity.inject(): Lazy<T> = lazy { getKoin().get<T>() }

/**
 * inject lazily given dependency for Activity - can be null
 */
inline fun <reified T> FragmentActivity.injectOrNull(): Lazy<T?> = lazy { getKoin().getOrNull<T>() }


/**
 * Return Koin context from Activity
 */
fun Fragment.getKoin(): KoinContext = this.activity.getKoin()

/**
 * Inject current dependency
 */
inline fun <reified T> Fragment.get(): T = getKoin().get<T>()

/**
 * Release scope instances
 */
fun Fragment.release(vararg scopeClasses: KClass<*>) = getKoin().release(*scopeClasses)

/**
 * inject lazily given dependency for Activity
 */
inline fun <reified T> Fragment.inject(): Lazy<T> = lazy { getKoin().get<T>() }

/**
 * inject lazily given dependency for Activity - can be null
 */
inline fun <reified T> Fragment.injectOrNull(): Lazy<T?> = lazy { getKoin().getOrNull<T>() }