/*
 * Copyright 2017-2018 the original author or authors.
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
package org.koin.reflect

import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext

/**
 * KoinComponent extensions to help for ViewModel
 *
 * @author Arnaud Giuliani
 */

/**
 * Resolve an instance by its canonical name
 *
 * @param class
 * @param module
 * @param parameters
 * @param filter - additional filter on definitions
 */
fun <T> KoinComponent.getByClass(
    clazz: Class<T>,
    module: String? = null,
    parameters: ParameterDefinition,
    filter: DefinitionFilter? = null
): T =
    (StandAloneContext.koinContext as KoinContext).getByCanonicalName(clazz.canonicalName, module, parameters, filter)

/**
 * Resolve an instance by its canonical typeName
 *
 * @param typeName
 * @param module
 * @param parameters
 * @param filter - additional filter on definitions
 */
fun <T> KoinComponent.getByTypeName(
    typeName: String,
    module: String? = null,
    parameters: ParameterDefinition,
    filter: DefinitionFilter? = null
): T =
    (StandAloneContext.koinContext as KoinContext).getByTypeName(typeName, module, parameters, filter)

typealias DefinitionFilter = (BeanDefinition<*>) -> Boolean