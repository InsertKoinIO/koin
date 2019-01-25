package org.koin.androidx.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import org.koin.core.KoinComponent

fun <T : ViewModel> LifecycleOwner.resolveViewModelInstance(parameters: ViewModelParameters<T>): T {
    val vmStore: ViewModelStore = getViewModelStore(parameters)

    val viewModelProvider = makeViewModelProvider(vmStore, parameters)

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
        this is FragmentActivity -> this.viewModelStore
        this is Fragment -> this.viewModelStore
        else -> error("Can't getByClass ViewModel '${parameters.clazz}' on $this - Is not a FragmentActivity nor a Fragment neither a valid ViewModelStoreOwner")
    }
}

private fun <T : ViewModel> makeViewModelProvider(
    vmStore: ViewModelStore,
    parameters: ViewModelParameters<T>
): ViewModelProvider {
    return ViewModelProvider(
        vmStore,
        object : ViewModelProvider.Factory, KoinComponent {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return getKoin().get(parameters.clazz, parameters.name, null, parameters.parameters)
            }
        })
}