package org.koin.android.viewmodel

import android.arch.lifecycle.*
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.logger.Level
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.time.measureDurationForResult

internal fun <T : ViewModel> ViewModelProvider.resolveInstance(viewModelParameters: ViewModelParameter<T>): T {
    val javaClass = viewModelParameters.clazz.java
    return if (logger.isAt(Level.DEBUG)) {
        logger.debug("!- ViewModelProvider getting instance")
        val (instance: T, duration: Double) = measureDurationForResult {
            get(viewModelParameters, viewModelParameters.qualifier, javaClass)
        }
        logger.debug("!- ViewModelProvider got instance in $duration")
        return instance
    } else {
        get(viewModelParameters, viewModelParameters.qualifier, javaClass)
    }
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
    return ViewModelProvider(
        viewModelParameters.viewModelStore,
        defaultViewModelFactory(viewModelParameters)
    )
}

class StateBundle(val defaultState: Bundle, val index: Int, val currentValues: Array<out Any?>)

private fun <T : ViewModel> getStateBundle(viewModelParameters: ViewModelParameter<T>): StateBundle? {
    val params = viewModelParameters.parameters?.invoke()
    val bundle = params?.values?.firstOrNull { it is Bundle } as? Bundle
    return bundle?.let { StateBundle(bundle, params.values.indexOf(bundle), params.values) }
}