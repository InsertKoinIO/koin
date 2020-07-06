package org.koin.androidx.viewmodel.parameter

import androidx.lifecycle.SavedStateHandle
import org.koin.core.parameter.DefinitionParameters
import kotlin.reflect.KClass

class StateDefinitionParameter(values: MutableList<Any> = arrayListOf()) : DefinitionParameters(values) {
    override fun <T> elementAt(i: Int, clazz: KClass<*>): T {
        return when {
            i == DEFAULT_INDEX && clazz == SavedStateHandle::class -> {
                super.elementAt(i, clazz)
            }
            i == DEFAULT_INDEX -> {
                throw WrongStateDefinitionParameterException(
                    "Try to inject SavedStateHandle into $clazz. Please check your parameters: $values")
            }
            clazz == SavedStateHandle::class -> {
                throw WrongStateDefinitionParameterException(
                    "Try to inject SavedStateHandle into position $i but should be $DEFAULT_INDEX. Please check your parameters to add SavedStateHandle first: $values")
            }
            else -> super.elementAt(i, clazz)
        }
    }

    companion object {
        const val DEFAULT_INDEX = 0
        fun from(params: DefinitionParameters) = StateDefinitionParameter(params.values)
    }
}