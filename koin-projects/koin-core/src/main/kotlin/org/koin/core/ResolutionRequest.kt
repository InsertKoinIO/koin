package org.koin.core

import org.koin.core.bean.BeanRegistry
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
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
    val module: String? = null,
    val parameters: ParameterDefinition
) : ResolutionRequest()

/**
 * Java Class instance Request
 */
data class ClassRequest(
    val name: String = "",
    val clazz: Class<*>,
    val module: String? = null,
    val parameters: ParameterDefinition
) : ResolutionRequest()

/**
 * Custom filter instance Request
 */
data class CustomRequest(
    val defininitionFilter: DefinitionFilter,
    val module: String? = null,
    val clazz : Class<*>,
    val parameters: ParameterDefinition
) : ResolutionRequest()

typealias DefinitionFilter = (BeanRegistry) -> List<BeanDefinition<*>>