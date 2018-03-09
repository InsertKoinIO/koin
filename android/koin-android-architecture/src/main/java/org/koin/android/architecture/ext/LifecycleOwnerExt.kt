package org.koin.android.architecture.ext

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.Koin
import org.koin.core.parameter.ParameterMap
import kotlin.reflect.KClass


/**
 * Lazy get a viewModel instance
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel definition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> LifecycleOwner.viewModel(
    key: String? = null,
    name: String? = null,
    noinline parameters: ParameterMap = { emptyMap() }
): Lazy<T> {
    return viewModelByClass(false, T::class, key, name, parameters)
}

/**
 * Lazy get a viewModel instance shared with Activity
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel definition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Fragment.sharedViewModel(
    key: String? = null,
    name: String? = null,
    noinline parameters: ParameterMap = { emptyMap() }
): Lazy<T> {
    return viewModelByClass(true, T::class, key, name, parameters)
}

/**
 * Lazy get a viewModel instance
 *
 * @param fromActivity - create it from Activity (default true)
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel definition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
fun <T : ViewModel> LifecycleOwner.viewModelByClass(
    fromActivity: Boolean,
    clazz: KClass<T>,
    key: String? = null,
    name: String? = null,
    parameters: ParameterMap = { emptyMap() }
): Lazy<T> {
    return lazy { getViewModelByClass(fromActivity, clazz, key, name, parameters) }
}

/**
 * Get a viewModel instance
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel definition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> LifecycleOwner.getViewModel(
    key: String? = null,
    name: String? = null,
    noinline parameters: ParameterMap = { emptyMap() }
): T {
    return getViewModelByClass(false, T::class, key, name, parameters)
}

/**
 * Get a shared viewModel instance from underlying Activity
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel definition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Fragment.getSharedViewModel(
    key: String? = null,
    name: String? = null,
    noinline parameters: ParameterMap = { emptyMap() }
): T {
    return getViewModelByClass(true, T::class, key, name, parameters)
}

/**
 * Get a viewModel instance
 *
 * @param fromActivity - create it from Activity (default false) - not used if on Activity
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel definition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
fun <T : ViewModel> LifecycleOwner.getViewModelByClass(
    fromActivity: Boolean = false,
    clazz: KClass<T>,
    key: String? = null,
    name: String? = null,
    parameters: ParameterMap = { emptyMap() }
): T {
    KoinFactory.apply {
        this.parameters = parameters
        this.name = name
    }
    val viewModelProvider = when {
        this is FragmentActivity -> {
            Koin.logger.log("[ViewModel] get for FragmentActivity @ $this")
            ViewModelProvider(ViewModelStores.of(this), KoinFactory)
        }
        this is Fragment -> {
            if (fromActivity) {
                Koin.logger.log("[ViewModel] get for FragmentActivity @ ${this.activity}")
                ViewModelProvider(ViewModelStores.of(this.activity), KoinFactory)
            } else {
                Koin.logger.log("[ViewModel] get for Fragment @ $this")
                ViewModelProvider(ViewModelStores.of(this), KoinFactory)
            }
        }
        else -> error("Can't get ViewModel on $this - Is not a FragmentActivity nor a Fragment")
    }
    return if (key != null) viewModelProvider.get(
        key,
        clazz.java
    ) else viewModelProvider.get(clazz.java)
}
