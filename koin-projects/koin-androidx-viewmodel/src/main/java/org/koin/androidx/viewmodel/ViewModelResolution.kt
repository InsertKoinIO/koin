package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.ext.android.getViewModelStore
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.logger.Level
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.time.measureDurationForResult

/**
 * Resolve ViewModel instance
 * @param viewModelParameters
 */
fun <T : ViewModel> ViewModelProvider.resolveInstance(viewModelParameters: ViewModelParameter<T>): T {
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

/**
 * Retrieve ViewModel from Android factory
 */
fun <T : ViewModel> ViewModelProvider.get(
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

/**
 * Create ViewModelProvider
 * @param viewModelStore
 * @param viewModelParameters
 */
fun <T : ViewModel> Scope.createViewModelProvider(
    viewModelParameters: ViewModelParameter<T>
): ViewModelProvider {
    return ViewModelProvider(
        viewModelParameters.owner.getViewModelStore(),
        if (viewModelParameters.state != null) {
            stateViewModelFactory(viewModelParameters)
        } else {
            defaultViewModelFactory(viewModelParameters)
        }
    )
}