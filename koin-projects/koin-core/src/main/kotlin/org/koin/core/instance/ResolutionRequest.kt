package org.koin.core.instance

import org.koin.core.parameter.ParameterDefinition
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

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
    val clazz: KClass<*>,
    val parameters: ParameterDefinition
) : ResolutionRequest()

