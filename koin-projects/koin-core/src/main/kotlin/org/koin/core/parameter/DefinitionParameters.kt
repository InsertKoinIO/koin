/*
 * Copyright 2017-2020 the original author or authors.
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

import org.koin.core.error.DefinitionParameterException
import org.koin.core.error.NoParameterFoundException
import org.koin.core.parameter.DefinitionParameters.Companion.MAX_PARAMS
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * DefinitionParameters - Parameter holder
 * Usable with exploded declaration
 *
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
open class DefinitionParameters(val values: List<Any> = listOf()) {

    open fun <T> elementAt(i: Int, clazz: KClass<*>): T =
        if (values.size > i) values[i] as T else throw NoParameterFoundException(
            "Can't get injected parameter #$i from $this for type '${clazz.getFullName()}'")

    inline operator fun <reified T> component1(): T = elementAt(0, T::class)
    inline operator fun <reified T> component2(): T = elementAt(1, T::class)
    inline operator fun <reified T> component3(): T = elementAt(2, T::class)
    inline operator fun <reified T> component4(): T = elementAt(3, T::class)
    inline operator fun <reified T> component5(): T = elementAt(4, T::class)

    /**
     * get element at given index
     * return T
     */
    operator fun <T> get(i: Int) = values[i] as T

    fun <T> set(i: Int, t: T) {
        values.toMutableList()[i] = t as Any
    }

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

    fun insert(index: Int, value: Any): DefinitionParameters {
        val (start, end) = values.partition { element -> values.indexOf(element) < index }
        return DefinitionParameters(start + value + end)
    }

    fun add(value: Any): DefinitionParameters {
        return insert(size(), value)
    }

    /**
     * Get first element of given type T
     * return T
     */
    inline fun <reified T> get(): T = values.first { it is T } as T

    /**
     * Get first element of given type T
     * return T
     */
    fun <T> getOrNull(clazz: KClass<*>): T? {
        val values = values.filter { it::class == clazz }
        return when (values.size) {
            1 -> values.first() as T
            0 -> null
            else -> throw DefinitionParameterException(
                "Ambiguous parameter injection: more than one value of type '${clazz.getFullName()}' to get from $this. Check your injection parameters")
        }
    }

    companion object {
        const val MAX_PARAMS = 5
    }

    override fun toString(): String = "DefinitionParameters${values.toList()}"
}

/**
 * Build a DefinitionParameters
 *
 * @see parameters
 * return ParameterList
 */
fun parametersOf(vararg parameters: Any) =
    if (parameters.size <= MAX_PARAMS) DefinitionParameters(
        parameters.toMutableList()) else throw DefinitionParameterException(
        "Can't build DefinitionParameters for more than $MAX_PARAMS arguments")

/**
 *
 */
fun emptyParametersHolder() = DefinitionParameters()


/**
 * Help define a DefinitionParameters
 */
typealias ParametersDefinition = () -> DefinitionParameters