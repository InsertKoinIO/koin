package org.koin.core

import org.koin.core.bean.BeanRegistry
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import kotlin.reflect.KClass

/**
 * Instance Resolution request
 */
sealed class ResolutionRequest

data class InstanceRequest(
    val name: String = "",
    val clazz: KClass<*>,
    val module: String? = null,
    val parameters: ParameterDefinition
) : ResolutionRequest()

data class ClassRequest(
    val name: String = "",
    val clazz: Class<*>,
    val module: String? = null,
    val parameters: ParameterDefinition
) : ResolutionRequest()

data class CustomRequest(
    val defininitionFilter: DefinitionFilter,
    val module: String? = null,
    val clazz : Class<*>,
    val parameters: ParameterDefinition
) : ResolutionRequest()

typealias DefinitionFilter = (BeanRegistry) -> List<BeanDefinition<*>>