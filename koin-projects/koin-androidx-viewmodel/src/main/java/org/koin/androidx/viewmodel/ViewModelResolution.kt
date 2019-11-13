package org.koin.androidx.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

internal  fun <T : ViewModel> ViewModelProvider.get(
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

internal  fun <T : ViewModel> Scope.createViewModelProvider(
    viewModelParameters: ViewModelParameter<T>
): ViewModelProvider {
    val stateBundle: Bundle? = getStateBundle(viewModelParameters)

    return ViewModelProvider(
        viewModelParameters.viewModelStore,
        if (stateBundle != null) {
            stateViewModelFactory(viewModelParameters,stateBundle)
        } else {
            defaultViewModelFactory(viewModelParameters)
        }
    )
}

private fun <T : ViewModel> getStateBundle(viewModelParameters: ViewModelParameter<T>): Bundle? {
    val params = viewModelParameters.parameters?.invoke()
    return params?.values?.firstOrNull { it is Bundle } as? Bundle
}