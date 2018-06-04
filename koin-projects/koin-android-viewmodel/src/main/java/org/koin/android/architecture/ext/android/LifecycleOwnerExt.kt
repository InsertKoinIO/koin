package org.koin.android.architecture.ext.android

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.android.architecture.KoinFactory
import org.koin.core.Koin
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import kotlin.reflect.KClass


/**
 * Lazy get a viewModel instance
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> LifecycleOwner.viewModel(
    key: String? = null,
    name: String? = null,
    module: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = viewModelByClass(false, T::class, key, name, module, parameters)

/**
 * Lazy get a viewModel instance
 *
 * @param fromActivity - create it from Activity (default true)
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
fun <T : ViewModel> LifecycleOwner.viewModelByClass(
    fromActivity: Boolean,
    clazz: KClass<T>,
    key: String? = null,
    name: String? = null,
    module: String? = null,
    parameters: ParameterDefinition = emptyParameterDefinition()
) = lazy { getViewModelByClass(fromActivity, clazz, key, name, module, parameters) }

/**
 * Get a viewModel instance
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> LifecycleOwner.getViewModel(
    key: String? = null,
    name: String? = null,
    module: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
): T {
    return getViewModelByClass(false, T::class, key, name, module, parameters)
}

/**
 * Get a viewModel instance
 *
 * @param fromActivity - create it from Activity (default false) - not used if on Activity
 * @param clazz - Class of the BeanDefinition to retrieve
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
fun <T : ViewModel> LifecycleOwner.getViewModelByClass(
    fromActivity: Boolean = false,
    clazz: KClass<T>,
    key: String? = null,
    name: String? = null,
    module: String? = null,
    parameters: ParameterDefinition = emptyParameterDefinition()
): T {
    KoinFactory.apply {
        KoinFactory.parameters = parameters
        KoinFactory.name = name
        KoinFactory.module = module
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
