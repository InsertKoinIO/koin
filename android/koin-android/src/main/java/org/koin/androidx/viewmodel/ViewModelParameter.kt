package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

@Deprecated("Deprecated API in favor of KoinViewModelFactory")
@KoinInternalApi
class ViewModelParameter<T : Any>(
    val clazz: KClass<T>,
    val qualifier: Qualifier? = null,
    val state: BundleDefinition? = null,
    val parameters: ParametersDefinition? = null,
    val viewModelStoreOwner: ViewModelStoreOwner,
    val registryOwner: SavedStateRegistryOwner? = null
)