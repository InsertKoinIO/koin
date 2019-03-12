package org.koin.android.viewmodel

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModelStoreOwner
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.ScopeInstance
import kotlin.reflect.KClass

class ViewModelParameters<T : Any>(
        val clazz: KClass<T>,
        val owner: LifecycleOwner,
        val scope: ScopeInstance = ScopeInstance.GLOBAL,
        val qualifier: Qualifier? = null,
        val from: ViewModelStoreOwnerDefinition? = null,
        val parameters: ParametersDefinition? = null
)

typealias ViewModelStoreOwnerDefinition = () -> ViewModelStoreOwner