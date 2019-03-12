package org.koin.android.viewmodel

import android.arch.lifecycle.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.core.Koin

/**
 * resolve instance
 * @param parameters
 */
fun <T : ViewModel> Koin.getViewModel(parameters: ViewModelParameters<T>): T {
    val vmStore: ViewModelStore = parameters.owner.getViewModelStore(parameters)
    val viewModelProvider = createViewModelProvider(vmStore, parameters)
    return viewModelProvider.getInstance(parameters)
}

/**
 * resolve instance
 * @param parameters
 */
fun <T : ViewModel> Koin.injectViewModel(parameters: ViewModelParameters<T>): Lazy<T> = lazy { getViewModel(parameters) }

private fun <T : ViewModel> ViewModelProvider.getInstance(parameters: ViewModelParameters<T>): T = this.get(parameters.clazz.java)

private fun <T : ViewModel> LifecycleOwner.getViewModelStore(
        parameters: ViewModelParameters<T>
): ViewModelStore =
        when {
            parameters.from != null -> parameters.from.invoke().viewModelStore
            this is FragmentActivity -> ViewModelStores.of(this)
            this is Fragment -> ViewModelStores.of(this)
            else -> error("Can't getByClass ViewModel '${parameters.clazz}' on $this - Is not a FragmentActivity nor a Fragment neither a valid ViewModelStoreOwner")
        }

private fun <T : ViewModel> Koin.createViewModelProvider(
        vmStore: ViewModelStore,
        parameters: ViewModelParameters<T>
): ViewModelProvider {
    return ViewModelProvider(
            vmStore,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return get(parameters.clazz, parameters.qualifier, parameters.scope, parameters.parameters)
                }
            })
}