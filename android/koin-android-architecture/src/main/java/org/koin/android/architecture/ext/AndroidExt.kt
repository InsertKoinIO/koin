package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.standalone.KoinComponent


/**
 * Retrieve given ViewModel for Android Fragment
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(T::class.java)
}

/**
 * Retrieve given ViewModel for Android FragmentActivity
 */
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(T::class.java)
}

/**
 * inject lazily given viewModel for Android Fragment
 */
inline fun <reified T : ViewModel> Fragment.injectViewModel(): Lazy<T> = lazy {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    vm.get(T::class.java)
}

/**
 * inject lazily given viewModel for Android FragmentActivity
 */
inline fun <reified T : ViewModel> FragmentActivity.injectViewModel(): Lazy<T> = lazy {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    vm.get(T::class.java)
}

object KoinFactory : ViewModelProvider.Factory, KoinComponent {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return get(modelClass)
    }
}