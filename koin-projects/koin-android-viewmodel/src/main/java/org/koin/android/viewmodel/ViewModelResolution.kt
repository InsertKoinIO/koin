package org.koin.android.viewmodel

import android.arch.lifecycle.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.core.context.GlobalContext

fun <T : ViewModel> LifecycleOwner.resolveViewModelInstance(
        parameters: ViewModelParameters<T>,
        koin: Koin = GlobalContext.get().koin): T {
    val vmStore: ViewModelStore = getViewModelStore(parameters)

    val viewModelProvider = makeViewModelProvider(vmStore, parameters,koin)

    return viewModelProvider.getInstance(parameters)
}

private fun <T : ViewModel> ViewModelProvider.getInstance(
    parameters: ViewModelParameters<T>
): T {
    return this.get(parameters.clazz.java)
}

private fun <T : ViewModel> LifecycleOwner.getViewModelStore(
    parameters: ViewModelParameters<T>
): ViewModelStore {
    return when {
        parameters.from != null -> parameters.from.invoke().viewModelStore
        this is FragmentActivity -> ViewModelStores.of(this)
        this is Fragment -> ViewModelStores.of(this)
        else -> error("Can't getByClass ViewModel '${parameters.clazz}' on $this - Is not a FragmentActivity nor a Fragment neither a valid ViewModelStoreOwner")
    }
}

private fun <T : ViewModel> makeViewModelProvider(
    vmStore: ViewModelStore,
    parameters: ViewModelParameters<T>,
    koin: Koin = GlobalContext.get().koin
): ViewModelProvider {
    return ViewModelProvider(
        vmStore,
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return koin.get(parameters.clazz, parameters.name, null, parameters.parameters)
            }
        })
}