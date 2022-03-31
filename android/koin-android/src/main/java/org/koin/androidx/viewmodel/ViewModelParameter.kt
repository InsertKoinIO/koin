package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModelStore
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

class ViewModelParameter<T : Any>(
    val clazz: KClass<T>,
    val qualifier: Qualifier? = null,
    val state: BundleDefinition? = null,
    val parameters: ParametersDefinition? = null,
    val viewModelStore: ViewModelStore,
    val registryOwner: SavedStateRegistryOwner? = null
)