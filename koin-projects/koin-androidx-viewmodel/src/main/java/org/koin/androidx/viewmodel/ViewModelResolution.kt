package org.koin.androidx.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.androidx.viewmodel.dsl.isStateViewModel
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.logger.Level
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.core.time.measureDuration

/**
 * resolve instance
 * @param parameters
 */
fun <T : ViewModel> Koin.getViewModel(parameters: ViewModelParameters<T>): T {
    val vmStore: ViewModelStore = parameters.owner.getViewModelStore(parameters)
    val viewModelProvider = rootScope.createViewModelProvider(vmStore, parameters)
    return viewModelProvider.getInstance(parameters)
}

fun <T : ViewModel> ViewModelProvider.getInstance(parameters: ViewModelParameters<T>): T {
    val javaClass = parameters.clazz.java
    return if (KoinApplication.logger.isAt(Level.DEBUG)) {
        logger.debug("!- ViewModelProvider getting instance")
        val (instance: T, duration: Double) = measureDuration {
            if (parameters.qualifier != null) {
                this.get(parameters.qualifier.toString(), javaClass)
            } else {
                this.get(javaClass)
            }
        }
        logger.debug("!- ViewModelProvider got instance in $duration")
        return instance
    } else {
        if (parameters.qualifier != null) {
            this.get(parameters.qualifier.toString(), javaClass)
        } else {
            this.get(javaClass)
        }
    }
}

fun <T : ViewModel> LifecycleOwner.getViewModelStore(
        parameters: ViewModelParameters<T>
): ViewModelStore =
        when {
            parameters.from != null -> parameters.from.invoke().viewModelStore
            this is FragmentActivity -> this.viewModelStore
            this is Fragment -> this.viewModelStore
            else -> error("Can't getByClass ViewModel '${parameters.clazz}' on $this - Is not a FragmentActivity nor a Fragment neither a valid ViewModelStoreOwner")
        }

fun <T : ViewModel> Scope.createViewModelProvider(
        vmStore: ViewModelStore,
        parameters: ViewModelParameters<T>
): ViewModelProvider {
    return ViewModelProvider(
            vmStore,
            if(beanRegistry.findDefinition(parameters.qualifier, parameters.clazz)?.isStateViewModel() == true) {
                object : AbstractSavedStateViewModelFactory(parameters.owner as SavedStateRegistryOwner, parameters.defaultArguments()) {
                    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
                        return get(parameters.clazz, parameters.qualifier) {
                            parametersOf(handle, *(parameters.parameters?.invoke() ?: emptyParametersHolder()).values)
                        }
                    }
                }
            } else {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return get(parameters.clazz, parameters.qualifier, parameters.parameters)
                    }
                }})
}