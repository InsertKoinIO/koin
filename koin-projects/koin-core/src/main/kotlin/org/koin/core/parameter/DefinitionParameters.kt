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
package org.koin.core.parameter

import org.koin.core.error.NoParameterFoundException
import org.koin.core.parameter.DefinitionParameters.Companion.MAX_PARAMS

/**
 * DefinitionParameters - Parameter holder
 * Usable with exploded declaration
 *
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
class DefinitionParameters internal constructor(vararg val values: Any?) {

    private fun <T> elementAt(i: Int): T =
        if (values.size > i) values[i] as T else throw NoParameterFoundException("Can't get parameter value #$i from $this")

    operator fun <T> component1(): T = elementAt(0)
    operator fun <T> component2(): T = elementAt(1)
    operator fun <T> component3(): T = elementAt(2)
    operator fun <T> component4(): T = elementAt(3)
    operator fun <T> component5(): T = elementAt(4)

    /**
     * get element at given index
     * return T
     */
    operator fun <T> get(i: Int) = values[i] as T

    /**
     * Number of contained elements
     */
    fun size() = values.size

    /**
     * Tells if it has no parameter
     */
    fun isEmpty() = size() == 0

    /**
     * Tells if it has parameters
     */
    fun isNotEmpty() = !isEmpty()

    /**
     * Get first element of given type T
     * return T
     */
    inline fun <reified T> get() = values.first { it is T }

    companion object {
        const val MAX_PARAMS = 5
    }
}

/**
 * Build a DefinitionParameters
 *
 * @see parameters
 * return ParameterList
 */
fun parametersOf(vararg parameters: Any?) =
    if (parameters.size <= MAX_PARAMS) DefinitionParameters(*parameters) else error("Can't build DefinitionParameters for more than $MAX_PARAMS arguments")

/**
 *
 */
fun emptyParametersHolder() = DefinitionParameters()

/**
 * Help define a DefinitionParameters
 */
typealias ParametersDefinition = () -> DefinitionParameters