package org.koin.androidx.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.core.error.DefinitionParameterException
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

/**
 * Create Bundle/State ViewModel Factory
 */
fun <T : ViewModel> Scope.stateViewModelFactory(
        vmParams: ViewModelParameter<T>
): AbstractSavedStateViewModelFactory {
    val registryOwner = (vmParams.stateRegistryOwner
            ?: error("Can't create SavedStateViewModelFactory without a proper stateRegistryOwner"))
    return object : AbstractSavedStateViewModelFactory(registryOwner, vmParams.bundle) {
        override fun <T : ViewModel?> create(
                key: String, modelClass: Class<T>, handle: SavedStateHandle
        ): T {
            return get(
                    vmParams.clazz,
                    vmParams.qualifier
            ) { parametersOf(*insertStateParameter(handle)) }
        }

        private fun insertStateParameter(handle: SavedStateHandle): Array<out Any?> {
            val parameters: DefinitionParameters = vmParams.parameters?.invoke() ?: emptyParametersHolder()
            val values = parameters.values.toMutableList()
            if (values.size > 4) {
                throw DefinitionParameterException("Can't add SavedStateHandle to your definition function parameters, as you already have ${values.size} elements: $values")
            }

            values.add(0, handle)
            return values.toTypedArray()
        }
    }
}

/**
 * Create Default ViewModel Factory
 */
fun <T : ViewModel> Scope.defaultViewModelFactory(parameters: ViewModelParameter<T>): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return get(parameters.clazz, parameters.qualifier, parameters.parameters)
        }
    }
}