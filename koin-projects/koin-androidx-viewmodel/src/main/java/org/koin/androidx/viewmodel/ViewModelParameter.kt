package org.koin.androidx.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModelStore
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

class ViewModelParameter<T : Any>(
        val clazz: KClass<T>,
        val qualifier: Qualifier? = null,
        val parameters: ParametersDefinition? = null,
        val initialState: Bundle? = null,
        val viewModelStore: ViewModelStore,
        val registryOwner: SavedStateRegistryOwner? = null
)