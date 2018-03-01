package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.ParameterMap
import org.koin.standalone.KoinComponent
import kotlin.reflect.KClass

/**
 * Get a ViewModel for given Fragment
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(key: String? = null, parameters: ParameterMap = emptyMap()): T {
    return getViewModel(T::class, key, parameters)
}

/**
 * Get a ViewModel for given Fragment
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> Fragment.getViewModel(clazz: KClass<T>, key: String? = null, parameters: ParameterMap = emptyMap()): T {
    KoinFactory.parameters = parameters
    return ViewModelProvider(ViewModelStores.of(this), KoinFactory).getK(key, clazz)
}

/**
 * Lazy get view model
 * @param fromActivity - reuse ViewModel from parent Activity or create new one
 */
inline fun <reified T : ViewModel> Fragment.viewModel(fromActivity: Boolean = true, key: String? = null, parameters: ParameterMap = emptyMap()): Lazy<T> = viewModel(T::class, fromActivity, key, parameters)

/**
 * Lazy get view model
 * version that avoid inline parameter and take KClass to instantiate
 *
 * @param fromActivity - reuse ViewModel from parent Activity or create new one
 */
fun <T : ViewModel> Fragment.viewModel(clazz: KClass<T>, fromActivity: Boolean = true, key: String? = null, parameters: ParameterMap = emptyMap()): Lazy<T> = lazy {
    val currentActivity = activity
    if (fromActivity && currentActivity != null) currentActivity.getViewModel(clazz, key, parameters) else getViewModel(clazz, key, parameters)
}

/**
 * Resolve and get ViewModel by type and key (given non null key parameter)
 *
 * @param key The key to use to identify the ViewModel
 */
internal fun <T : ViewModel> ViewModelProvider.getK(key: String? = null, clazz: KClass<T>): T {
    return if (key == null) {
        get(clazz.java)
    } else {
        get(key, clazz.java)
    }
}

/**
 * Get a ViewModel for given Activity
 */
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(key: String? = null, parameters: ParameterMap): T {
    return getViewModel(T::class, key, parameters)
}

/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> FragmentActivity.getViewModel(clazz: KClass<T>, key: String? = null, parameters: ParameterMap): T {
    KoinFactory.parameters = parameters
    return ViewModelProvider(ViewModelStores.of(this), KoinFactory).getK(key, clazz)
}

/**
 * Lazy get a ViewModel - for Activity
 */
inline fun <reified T : ViewModel> FragmentActivity.viewModel(key: String? = null, parameters: ParameterMap = emptyMap()): Lazy<T> = lazy { getViewModel<T>(key, parameters) }

/**
 * Lazy get a ViewModel - for Activity
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> FragmentActivity.viewModel(clazz: KClass<T>, key: String? = null, parameters: ParameterMap = emptyMap()): Lazy<T> = lazy { getViewModel(clazz, key, parameters) }

/**
 * Koin ViewModel factory
 */
object KoinFactory : ViewModelProvider.Factory, KoinComponent {

    internal var parameters: ParameterMap = emptyMap()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return get(modelClass, parameters)
    }
}
