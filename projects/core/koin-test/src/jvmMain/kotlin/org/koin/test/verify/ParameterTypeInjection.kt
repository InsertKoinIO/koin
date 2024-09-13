package org.koin.test.verify

import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.reflect.KClass

/**
 * ParameterTypeInjection is a proposal to allow describe types that are dynamic, and needs injection parameters (use of parametersOf)
 *
 * @author Arnaud Giuliani
 */

/**
 * Define Parameter Injection Types in order to help verify definition
 */
@KoinExperimentalAPI
data class ParameterTypeInjection(val targetType : KClass<*>, val injectedTypes : List<KClass<*>>)

/**
 * Define injection for a definition Type
 * @param T - definition type
 * @param injectedParameterTypes - Types that need to be injected later with parametersOf
 */
@KoinExperimentalAPI
inline fun <reified T> definition(vararg injectedParameterTypes : KClass<*>): ParameterTypeInjection{
    return ParameterTypeInjection(T::class, injectedParameterTypes.toList())
}

/**
 * Declare list of ParameterTypeInjection - in order to help define parmater injection types to allow in verify
 * @param injectionType - list of ParameterTypeInjection
 */
@KoinExperimentalAPI
fun injectedParameters(vararg injectionType : ParameterTypeInjection) : List<ParameterTypeInjection>{
    return injectionType.toList()
}