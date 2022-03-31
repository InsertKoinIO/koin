package org.koin.androidx.viewmodel

import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import org.koin.androidx.viewmodel.scope.BundleDefinition
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

class ViewModelParameter<T : Any>(
    val clazz: KClass<T>,
    val qualifier: Qualifier? = null,
    //TODO Clean for noinline extrasProducer: (() -> CreationExtras)? = null,
    val state: BundleDefinition? = null,
    val parameters: ParametersDefinition? = null,
    //TODO Clean up - only for factory pick
    val viewModelStoreOwner: ViewModelStoreOwner,
    val registryOwner: SavedStateRegistryOwner? = null
)