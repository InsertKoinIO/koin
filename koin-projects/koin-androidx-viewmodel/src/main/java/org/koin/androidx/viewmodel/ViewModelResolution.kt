package org.koin.androidx.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

internal fun <T : ViewModel> ViewModelProvider.resolveInstance(viewModelParameters: ViewModelParameter<T>): T {
    val javaClass = viewModelParameters.clazz.java
    return get(viewModelParameters, viewModelParameters.qualifier, javaClass)
}

internal fun <T : ViewModel> ViewModelProvider.get(
        viewModelParameters: ViewModelParameter<T>,
        qualifier: Qualifier?,
        javaClass: Class<T>
): T {
    return if (viewModelParameters.qualifier != null) {
        get(qualifier.toString(), javaClass)
    } else {
        get(javaClass)
    }
}

internal fun <T : ViewModel> Scope.createViewModelProvider(
        viewModelParameters: ViewModelParameter<T>
): ViewModelProvider {
    val stateBundle: StateBundle? = getStateBundle(viewModelParameters)

    return ViewModelProvider(
            viewModelParameters.viewModelStore,
            if (stateBundle != null) {
                stateViewModelFactory(viewModelParameters, stateBundle)
            } else {
                defaultViewModelFactory(viewModelParameters)
            }
    )
}

class StateBundle(val defaultState: Bundle, val index: Int, val currentValues: Array<out Any?>)

private fun <T : ViewModel> getStateBundle(viewModelParameters: ViewModelParameter<T>): StateBundle? {
    val params = viewModelParameters.parameters?.invoke()
    val bundle = params?.values?.firstOrNull { it is Bundle } as? Bundle
    return bundle?.let { StateBundle(bundle, params.values.indexOf(bundle), params.values) }
}