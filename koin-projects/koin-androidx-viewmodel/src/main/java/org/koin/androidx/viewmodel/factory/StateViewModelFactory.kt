package org.koin.androidx.viewmodel.factory

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.androidx.viewmodel.parameter.StateDefinitionParameter
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.Scope

class StateViewModelFactory<T : ViewModel>(
        val scope: Scope,
        val parameters: ViewModelParameter<T>
) : AbstractSavedStateViewModelFactory(
        parameters.registryOwner
                ?: error("Can't create SavedStateViewModelFactory without a proper stateRegistryOwner"),
        parameters.initialState
) {
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return scope.get(
                parameters.clazz,
                parameters.qualifier
        ) {
            val definitionParameters: DefinitionParameters? = parameters.parameters?.invoke() ?: DefinitionParameters()
            when {
                definitionParameters != null -> {
                    StateDefinitionParameter.from(handle, definitionParameters)
                }
                else -> definitionParameters ?: emptyParametersHolder()
            }
        } as T
    }
}