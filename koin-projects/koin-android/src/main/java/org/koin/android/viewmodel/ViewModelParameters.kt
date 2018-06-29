package org.koin.android.viewmodel

import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition

/**
 * Data holder for ViewModel Factory
 */
data class ViewModelParameters(
    val name: String? = null,
    val module: String? = null,
    val parameters: ParameterDefinition = emptyParameterDefinition()
)