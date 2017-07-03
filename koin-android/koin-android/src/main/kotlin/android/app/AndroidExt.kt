package android.app

import org.koin.KoinContext
import org.koin.android.KoinContextAware
import org.koin.android.error.KoinApplicationException
import kotlin.reflect.KClass

/**
 * Return Koin context from Application
 */
@Throws(KoinApplicationException::class)
fun Application.getKoin(): KoinContext {
    if (this is KoinContextAware) {
        return getKoin()
    } else throw KoinApplicationException("Your application is not a Koin Application. Please use KoinContextAware interface in your application class.")
}

/**
 * Return Koin context from Activity
 */
fun Activity.getKoin(): KoinContext = this.application.getKoin()

/**
 * Inject current dependency
 */
inline fun <reified T> Activity.get(): T = getKoin().get<T>()


fun Activity.release(vararg scopeClasses: KClass<*>) = getKoin().release(*scopeClasses)

/**
 * inject lazily given dependency for Activity
 */
inline fun <reified T> Activity.inject(): Lazy<T> = lazy { getKoin().get<T>() }

/**
 * inject lazily given dependency for Activity - can be null
 */
inline fun <reified T> Activity.injectOrNull(): Lazy<T?> = lazy { getKoin().getOrNull<T>() }

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

fun Fragment.release(vararg scopeClasses: KClass<*>) = getKoin().release(*scopeClasses)
