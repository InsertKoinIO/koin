package org.koin.androidx.viewmodel.factory

import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.Scope

class StateViewModelFactory<T : ViewModel>(
    val scope: Scope,
    val parameters: ViewModelParameter<T>,
) : AbstractSavedStateViewModelFactory(
    parameters.registryOwner ?: error("Can't create SavedStateViewModelFactory without a proper stateRegistryOwner"),
    null
) {
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        val params: ParametersDefinition = addHandle(handle)
        return scope.get(
            parameters.clazz,
            parameters.qualifier,
            params
        )
    }

    private fun addHandle(handle: SavedStateHandle): ParametersDefinition {
        val definitionParameters = parameters.parameters?.invoke() ?: emptyParametersHolder()
        return { definitionParameters.add(handle) }
    }

    override fun onRequery(viewModel: ViewModel) {
        if (!scope.isRoot) {
            scope.refreshScopeInstance(
                parameters.clazz,
                parameters.qualifier,
                viewModel
            )
        }
        super.onRequery(viewModel)
    }
}