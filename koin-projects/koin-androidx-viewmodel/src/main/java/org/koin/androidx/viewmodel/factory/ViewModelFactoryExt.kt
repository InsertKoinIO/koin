package org.koin.androidx.viewmodel.factory

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.core.error.DefinitionParameterException
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.emptyParametersHolder
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope

fun <T : ViewModel> Scope.buildViewModelFactory(
    vmParams: ViewModelParameter<T>): ViewModelProvider.Factory {
    return object : AbstractSavedStateViewModelFactory(vmParams.registryOwner, vmParams.bundle) {
//        override fun <T : ViewModel?> create(
//            key: String, modelClass: Class<T>, handle: SavedStateHandle
//        ): T {
//            return get(
//                vmParams.clazz,
//                vmParams.qualifier
//            ) { parametersOf(*insertStateParameter(handle)) }
//        }
//
//        private fun insertStateParameter(handle: SavedStateHandle): Array<out Any?> {
//            val parameters: DefinitionParameters = vmParams.parameters?.invoke() ?: emptyParametersHolder()
//            val values = parameters.values.toMutableList()
//            if (values.size > 4) {
//                throw DefinitionParameterException(
//                    "Can't add SavedStateHandle to your definition function parameters, as you already have ${values.size} elements: $values")
//            }
//
//            values.add(0, handle)
//            return values.toTypedArray()
//        }
//    }
}