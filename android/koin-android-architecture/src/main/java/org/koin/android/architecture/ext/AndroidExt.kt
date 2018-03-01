package org.koin.android.architecture.ext

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.ParameterMap
import kotlin.reflect.KClass


/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
inline fun <reified T : ViewModel> LifecycleOwner.viewModel(key: String? = null, parameters: ParameterMap): Lazy<T> {
    return viewModel(T::class, key, parameters)
}

/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> LifecycleOwner.viewModel(clazz: KClass<T>, key: String? = null, parameters: ParameterMap): Lazy<T> {
    return lazy { getViewModel(clazz, key, parameters) }
}

/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
inline fun <reified T : ViewModel> LifecycleOwner.getViewModel(key: String? = null, parameters: ParameterMap): T {
    return getViewModel(T::class, key, parameters)
}

/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> LifecycleOwner.getViewModel(clazz: KClass<T>, key: String? = null, parameters: ParameterMap): T {
    KoinFactory.parameters = parameters
    val viewModelProvider = when {
        this is FragmentActivity -> ViewModelProvider(ViewModelStores.of(this), KoinFactory)
        this is Fragment -> ViewModelProvider(ViewModelStores.of(this), KoinFactory)
        else -> error("Can't get ViewModel on $this - Is not a FragmentActivity nor a Fragment")
    }
    return if (key != null) viewModelProvider.get(key, clazz.java) else viewModelProvider.get(clazz.java)
}
