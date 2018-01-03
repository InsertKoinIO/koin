package org.koin.android.architecture.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.standalone.KoinComponent


inline fun <reified T : ViewModel> Fragment.getViewModel(): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(T::class.java)
}

object KoinFactory : ViewModelProvider.Factory, KoinComponent {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return get(modelClass)
    }
}