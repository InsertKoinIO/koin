package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.standalone.KoinComponent

/**
 * Get a ViewModel for given Fragment
 * @param key The key to use to identify the ViewModel
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(key: String? = null): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(key)
}

/**
 * Lazy get view model
 * @param fromActivity - reuse ViewModel from parent Activity or create new one
 * @param key - the key to use to identify the ViewModel
 */
inline fun <reified T : ViewModel> Fragment.viewModel(fromActivity: Boolean = true, key: String? = null) = lazy { if (fromActivity) activity.getViewModel<T>(key) else getViewModel(key) }

/**
 * Get a ViewModel for given Activity
 * @param key The key to use to identify the ViewModel
 */
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(key: String? = null): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(key)
}

/**
 * Resolve and get ViewModel by type and key (given non null key parameter)
 * @param key The key to use to identify the ViewModel
 */
inline fun <reified T : ViewModel> ViewModelProvider.get(key: String? = null): T {
    return if (key == null) {
        get(T::class.java)
    } else {
        get(key, T::class.java)
    }
}

/**
 * Lazy get a ViewModel - for Activity
 * @param key The key to use to identify the ViewModel
 */
inline fun <reified T : ViewModel> FragmentActivity.viewModel(key: String? = null) = lazy { getViewModel<T>(key) }

/**
 * Koin ViewModel factory
 */
object KoinFactory : ViewModelProvider.Factory, KoinComponent {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return get(modelClass)
    }
}
