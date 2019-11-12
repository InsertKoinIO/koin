package org.koin.androidx.viewmodel

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

class ViewModelParameter<T : Any>(
    val clazz: KClass<T>,
    val qualifier: Qualifier? = null,
    val parameters: ParametersDefinition? = null,
    val owner: LifecycleOwner,
    val state: ViewModelStateDefinition? = null
)

typealias ViewModelOwnerDefinition = () -> LifecycleOwner
typealias ViewModelStateDefinition = () -> Bundle
