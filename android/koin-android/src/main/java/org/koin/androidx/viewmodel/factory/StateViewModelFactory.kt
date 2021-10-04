package androidx.lifecycle

import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.Scope

class StateViewModelFactory<T : ViewModel>(
    val scope: Scope,
    val parameters: ViewModelParameter<T>,
    val injectHandle: Boolean = true
) : AbstractSavedStateViewModelFactory(
    parameters.registryOwner ?: error("Can't create SavedStateViewModelFactory without a proper stateRegistryOwner"),
    parameters.initialState
) {
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        val params: ParametersDefinition? = if (injectHandle) {
            addHandle(handle)
        } else parameters.parameters
        return scope.get(
            parameters.clazz,
            parameters.qualifier,
            params
        )
    }

    private fun addHandle(handle: SavedStateHandle): ParametersDefinition {
        val definitionParameters = parameters.parameters?.invoke() ?: emptyParametersHolder()
        return { definitionParameters.insert(0, handle) }
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