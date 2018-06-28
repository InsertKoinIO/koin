package org.koin.androidx.architecture.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.Koin
import org.koin.core.parameter.Parameters
import org.koin.dsl.context.emptyParameters
import kotlin.reflect.KClass

/**
 * Lazy get a viewModel instance
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel definition of the same type)
 */
inline fun <reified T : ViewModel> LifecycleOwner.viewModel(
        key: String? = null,
        name: String? = null
): Lazy<T> {
    return viewModelByClass(false, T::class, key, name, emptyParameters())
}

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
        noinline parameters: Parameters
): Lazy<T> {
    return viewModelByClass(false, T::class, key, name, parameters)
}

/**
 * Lazy get a viewModel instance shared with Activity
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel definition of the same type)
 */
inline fun <reified T : ViewModel> Fragment.sharedViewModel(
        key: String? = null,
        name: String? = null
): Lazy<T> {
    return viewModelByClass(true, T::class, key, name, emptyParameters())
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
        noinline parameters: Parameters
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
        parameters: Parameters = emptyParameters()
): Lazy<T> {
    return lazy { getViewModelByClass(fromActivity, clazz, key, name, parameters) }
}

/**
 * Get a viewModel instance
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel definition of the same type)
 */
inline fun <reified T : ViewModel> LifecycleOwner.getViewModel(
        key: String? = null,
        name: String? = null
): T {
    return getViewModelByClass(false, T::class, key, name, emptyParameters())
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
        noinline parameters: Parameters
): T {
    return getViewModelByClass(false, T::class, key, name, parameters)
}

/**
 * Get a shared viewModel instance from underlying Activity
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel definition of the same type)
 */
inline fun <reified T : ViewModel> Fragment.getSharedViewModel(
        key: String? = null,
        name: String? = null
): T {
    return getViewModelByClass(true, T::class, key, name, emptyParameters())
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
        noinline parameters: Parameters = emptyParameters()
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
        parameters: Parameters = emptyParameters()
): T {
    KoinFactory.apply {
        KoinFactory.parameters = parameters
        KoinFactory.name = name
    }
    val viewModelProvider = when {
        this is FragmentActivity -> {
            Koin.logger.log("[ViewModel] get for FragmentActivity @ $this")
            ViewModelProvider(this.viewModelStore, KoinFactory)
        }
        this is Fragment -> {
            if (fromActivity) {
                Koin.logger.log("[ViewModel] get for FragmentActivity @ ${this.activity}")
                ViewModelProvider(this.activity!!.viewModelStore, KoinFactory)
            } else {
                Koin.logger.log("[ViewModel] get for Fragment @ $this")
                ViewModelProvider(this.viewModelStore, KoinFactory)
            }
        }
        else -> error("Can't get ViewModel on $this - Is not a FragmentActivity nor a Fragment")
    }
    return if (key != null) viewModelProvider.get(
            key,
            clazz.java
    ) else viewModelProvider.get(clazz.java)
}