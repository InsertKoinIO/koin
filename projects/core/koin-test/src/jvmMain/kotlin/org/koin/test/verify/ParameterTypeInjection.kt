/*
 * Copyright 2017-Present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * Define injection for a definition Type
 * @param T - definition type
 * @param injectedParameterTypes - Types that need to be injected later with parametersOf
 */
@KoinExperimentalAPI
inline fun <reified T> definition(injectedParameterTypes : List<KClass<*>>): ParameterTypeInjection{
    return ParameterTypeInjection(T::class, injectedParameterTypes)
}

/**
 * Declare list of ParameterTypeInjection - in order to help define parmater injection types to allow in verify
 * @param injectionType - list of ParameterTypeInjection
 */
@KoinExperimentalAPI
fun injectedParameters(vararg injectionType : ParameterTypeInjection) : List<ParameterTypeInjection>{
    return injectionType.toList()
}