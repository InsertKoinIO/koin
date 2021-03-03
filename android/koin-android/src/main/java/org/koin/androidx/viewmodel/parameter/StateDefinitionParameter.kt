package org.koin.androidx.viewmodel.parameter

import androidx.lifecycle.SavedStateHandle
import org.koin.core.parameter.DefinitionParameters
import kotlin.reflect.KClass

class StateDefinitionParameter(val state: SavedStateHandle, values: List<Any?> = emptyList()) :
    DefinitionParameters(values) {
    override fun <T> getOrNull(clazz: KClass<*>): T? {
        return if (clazz == SavedStateHandle::class) {
            state as T?
        } else super.getOrNull(clazz)
    }

    companion object {
        fun from(state: SavedStateHandle, params: DefinitionParameters) =
            StateDefinitionParameter(state, params.values)
    }
}