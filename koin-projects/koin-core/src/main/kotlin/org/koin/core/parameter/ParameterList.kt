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


/**
 * ParameterList - List of parameter
 * Usable with exploded declaration
 *
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
class ParameterList(vararg value: Any) {

    val values: List<*> = value.toList()

    private fun <T> elementAt(i: Int) = if (values.size > i) values[i] as T else error("Parameter element #$i is empty")

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
     * Get first element of given type T
     * return T
     */
    inline fun <reified T> get() = values.first { it is T }

}

/**
 * Function to define a parameter list
 * @see ParameterList
 */
typealias ParameterDefinition = () -> ParameterList

/**
 * Help build a Parameter list
 * @see ParameterList
 * return ParameterList
 */
fun parametersOf(vararg value: Any) = ParameterList(*value)

/**
 * Empty Parameter List
 */
fun emptyParameterDefinition() = { ParameterList() }

