package org.koin.core

import org.koin.core.parameter.ParameterDefinition

/**
 * Bean Instance Resolution request
 * Help ask/request a given definition in Koin and retrieve its instance
 */
sealed class ResolutionRequest

/**
 * Simple instance request
 */
data class InstanceRequest(
    val name: String = "",
    val clazzName: String,
    val module: String? = null,
    val parameters: ParameterDefinition
) : ResolutionRequest()

