package org.koin.android.viewmodel

import android.arch.lifecycle.ViewModelStore
import android.os.Bundle
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

class ViewModelParameter<T : Any>(
    val clazz: KClass<T>,
    val qualifier: Qualifier? = null,
    val parameters: ParametersDefinition? = null,
    val viewModelStore: ViewModelStore
)