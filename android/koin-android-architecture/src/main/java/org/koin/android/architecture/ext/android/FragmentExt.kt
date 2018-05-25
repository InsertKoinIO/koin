package org.koin.android.architecture.ext.android

import android.arch.lifecycle.ViewModel
import android.support.v4.app.Fragment
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition

/**
 * Lazy get a viewModel instance shared with Activity
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Fragment.sharedViewModel(
    key: String? = null,
    name: String? = null,
    module: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = viewModelByClass(true, T::class, key, name, module, parameters)

/**
 * Get a shared viewModel instance from underlying Activity
 *
 * @param key - ViewModel Factory key (if have several instances from same ViewModel)
 * @param name - Koin BeanDefinition name (if have several ViewModel beanDefinition of the same type)
 * @param parameters - parameters to pass to the BeanDefinition
 */
inline fun <reified T : ViewModel> Fragment.getSharedViewModel(
    key: String? = null,
    name: String? = null,
    module: String? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = getViewModelByClass(true, T::class, key, name, module, parameters)
