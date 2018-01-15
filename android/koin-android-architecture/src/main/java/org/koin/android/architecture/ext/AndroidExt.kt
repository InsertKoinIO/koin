package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.standalone.KoinComponent


/**
 * Retrieve given ViewModel for Android Fragment
 * @param shareWithActivityViewModel - if the viewModel instance is shared between activity/fragment / optional (true by default)
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(shareWithActivityViewModel: Boolean = true): T {
    val vm = ViewModelProvider(
            if (shareWithActivityViewModel)
                ViewModelStores.of(activity!!)
            else
                ViewModelStores.of(this)
            , KoinFactory)
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
 * @param shareWithActivityViewModel - if the viewModel instance is shared between activity/fragment / optional (true by default)
 */
inline fun <reified T : ViewModel> Fragment.injectViewModel(shareWithActivityViewModel: Boolean = true): Lazy<T> = lazy {
    getViewModel<T>(shareWithActivityViewModel)
}

/**
 * inject lazily given viewModel for Android FragmentActivity
 */
inline fun <reified T : ViewModel> FragmentActivity.injectViewModel(): Lazy<T> = lazy {
    getViewModel<T>()
}

object KoinFactory : ViewModelProvider.Factory, KoinComponent {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return get(modelClass)
    }
}