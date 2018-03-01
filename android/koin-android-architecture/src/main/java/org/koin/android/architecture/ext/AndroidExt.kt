package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.ParameterMap
import org.koin.standalone.KoinComponent
import kotlin.reflect.KClass

/*
    ------------ Fragment ------------
 */

/**
 * Get a ViewModel for given Fragment
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(parameters: ParameterMap = emptyMap()): T {
    return getViewModel(T::class, parameters)
}

/**
 * Lazy get view model
 * @param fromActivity - reuse ViewModel from parent Activity or create new one
 */
inline fun <reified T : ViewModel> Fragment.viewModel(fromActivity: Boolean = true, parameters: ParameterMap = emptyMap()): Lazy<T> = viewModel(T::class, fromActivity, parameters)

/**
 * Lazy get view model
 * version that avoid inline parameter and take KClass to instantiate
 *
 * @param fromActivity - reuse ViewModel from parent Activity or create new one
 */
fun <T : ViewModel> Fragment.viewModel(clazz: KClass<T>, fromActivity: Boolean = true, parameters: ParameterMap = emptyMap()): Lazy<T> = lazy {
    val currentActivity = activity
    if (fromActivity && currentActivity != null) currentActivity.getViewModel(clazz, parameters) else getViewModel(clazz, parameters)
}

/**
 * Get a ViewModel for given Fragment
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> Fragment.getViewModel(clazz: KClass<T>, parameters: ParameterMap = emptyMap()): T {
    KoinFactory.parameters = parameters
    return ViewModelProvider(ViewModelStores.of(this), KoinFactory).get(clazz.java)
}

/*
    ------------ Activity ------------
 */

/**
 * Lazy get a ViewModel - for Activity
 */
inline fun <reified T : ViewModel> FragmentActivity.viewModel(parameters: ParameterMap = emptyMap()): Lazy<T> = lazy { getViewModel<T>(parameters) }

/**
 * Lazy get a ViewModel - for Activity
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> FragmentActivity.viewModel(clazz: KClass<T>, parameters: ParameterMap = emptyMap()): Lazy<T> = lazy { getViewModel(clazz, parameters) }


/**
 * Get a ViewModel for given Activity
 */
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(parameters: ParameterMap): T {
    return getViewModel(T::class, parameters)
}

/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> FragmentActivity.getViewModel(clazz: KClass<T>, parameters: ParameterMap): T {
    KoinFactory.parameters = parameters
    return ViewModelProvider(ViewModelStores.of(this), KoinFactory).get(clazz.java)
}
