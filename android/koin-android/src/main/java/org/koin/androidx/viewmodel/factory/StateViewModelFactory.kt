package androidx.lifecycle

import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.scope.Scope

class StateViewModelFactory<T : ViewModel>(
    val scope: Scope,
    val parameters: ViewModelParameter<T>,
) : AbstractSavedStateViewModelFactory(
    parameters.registryOwner ?: error("Can't create SavedStateViewModelFactory without a proper stateRegistryOwner"),
    parameters.state?.invoke()
) {
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        parameters.defaultArgs?.let { args ->
            args.keySet().forEach {
                handle[it] = args.get(it)
            }
        }
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
}