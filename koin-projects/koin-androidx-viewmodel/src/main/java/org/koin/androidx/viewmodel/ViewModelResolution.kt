package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import org.koin.androidx.viewmodel.dsl.isStateViewModel
import org.koin.core.KoinApplication.Companion.logger
import org.koin.core.logger.Level
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.time.measureDurationForResult

/**
 * Resolve ViewModel instance
 * @param viewModelParameters
 */
fun <T : ViewModel> ViewModelProvider.resolveInstance(viewModelParameters: ViewModelParameters<T>): T {
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
    viewModelParameters: ViewModelParameters<T>,
    qualifier: Qualifier?,
    javaClass: Class<T>
): T {
    return if (viewModelParameters.qualifier != null) {
        this.get(qualifier.toString(), javaClass)
    } else {
        this.get(javaClass)
    }
}

/**
 * Create ViewModelProvider
 * @param viewModelStore
 * @param viewModelParameters
 */
fun <T : ViewModel> Scope.createViewModelProvider(
    viewModelStore: ViewModelStore,
    viewModelParameters: ViewModelParameters<T>
): ViewModelProvider {
    val findDefinition = beanRegistry.findDefinition(viewModelParameters.qualifier, viewModelParameters.clazz)
    return ViewModelProvider(
        viewModelStore,
        if (findDefinition.isStateViewModel() && viewModelParameters.defaultArguments != null) {
            stateViewModelFactory(viewModelParameters, viewModelParameters.defaultArguments)
        } else {
            defaultViewModelFactory(viewModelParameters)
        }
    )
}