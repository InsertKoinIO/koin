package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.standalone.KoinComponent

/**
 * Get a ViewModel for given Fragment
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(T::class.java)
}

/**
 * Lazy get view model
 * @param fromActivity - reuse ViewModel from parent Activity or create new one
 */
inline fun <reified T : ViewModel> Fragment.viewModel(fromActivity: Boolean = true) = lazy { if (fromActivity) activity.getViewModel<T>() else getViewModel() }

/**
 * Get a ViewModel for given Activity
 */
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(T::class.java)
}

/**
 * Lazy get a ViewModel - for Activity
 */
inline fun <reified T : ViewModel> FragmentActivity.viewModel() = lazy { getViewModel<T>() }

/**
 * Koin ViewModel factory
 */
object KoinFactory : ViewModelProvider.Factory, KoinComponent {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return get(modelClass)
    }
}
