package org.koin.android.architecture.ext

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.Koin
import org.koin.ParameterMap
import kotlin.reflect.KClass


/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
inline fun <reified T : ViewModel> LifecycleOwner.viewModel(fromActivity: Boolean = true, key: String? = null, name: String? = null, parameters: ParameterMap = emptyMap()): Lazy<T> {
    return viewModelByClass(fromActivity, T::class, key, name, parameters)
}

/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> LifecycleOwner.viewModelByClass(fromActivity: Boolean = true, clazz: KClass<T>, key: String? = null, name: String? = null, parameters: ParameterMap = emptyMap()): Lazy<T> {
    return lazy { getViewModelByClass(fromActivity, clazz, key, name, parameters) }
}

/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
inline fun <reified T : ViewModel> LifecycleOwner.getViewModel(key: String? = null, name: String? = null, parameters: ParameterMap = emptyMap()): T {
    return getViewModelByClass(false, T::class, key, name, parameters)
}

/**
 * Get a ViewModel for given Activity - from given class
 * version that avoid inline parameter and take KClass to instantiate
 */
fun <T : ViewModel> LifecycleOwner.getViewModelByClass(fromActivity: Boolean = false, clazz: KClass<T>, key: String? = null, name: String? = null, parameters: ParameterMap = emptyMap()): T {
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
            }
            else {
                Koin.logger.log("[ViewModel] get for Fragment @ $this")
                ViewModelProvider(ViewModelStores.of(this), KoinFactory)
            }
        }
        else -> error("Can't get ViewModel on $this - Is not a FragmentActivity nor a Fragment")
    }
    return if (key != null) viewModelProvider.get(key, clazz.java) else viewModelProvider.get(clazz.java)
}
