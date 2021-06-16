/*
 * Copyright 2017-2021 the original author or authors.
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
import org.koin.core.parameter.ParametersHolder.Companion.MAX_PARAMS
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * DefinitionParameters - Parameter holder
 * Usable with exploded declaration
 *
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
open class ParametersHolder(
    @PublishedApi
    internal val _values: MutableList<Any?> = mutableListOf()
) {

    val values : List<Any?> get() = _values

    open fun <T> elementAt(i: Int, clazz: KClass<*>): T =
            if (_values.size > i) _values[i] as T else throw NoParameterFoundException(
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
    operator fun <T> get(i: Int) = _values[i] as T

    fun <T> set(i: Int, t: T) {
        _values[i] = t as Any
    }

    /**
     * Number of contained elements
     */
    fun size() = _values.size

    /**
     * Tells if it has no parameter
     */
    fun isEmpty() = size() == 0

    /**
     * Tells if it has parameters
     */
    fun isNotEmpty() = !isEmpty()

    fun insert(index: Int, value: Any): ParametersHolder {
        _values.add(index,value)
        return this
    }

    fun add(value: Any): ParametersHolder {
        _values.add(value)
        return this
    }

    /**
     * Get first element of given type T
     * return T
     */
    inline fun <reified T : Any> get(): T = getOrNull(T::class) ?: throw DefinitionParameterException("No value found for type '${T::class.getFullName()}'")

    /**
     * Get first element of given type T
     * return T
     */
    open fun <T> getOrNull(clazz: KClass<*>): T? {
        return _values.firstNotNullOfOrNull { value -> if (value != null && value::class == clazz) value as? T? else null }
    }

    /**
     * Get first element of given type T
     * return T
     */
    inline fun <reified T> getOrNull(): T? {
        return _values.firstNotNullOfOrNull { value -> if (value is T) value else null }
    }

    companion object {
        const val MAX_PARAMS = 5
    }

    override fun toString(): String = "DefinitionParameters${_values.toList()}"
}

/**
 * Build a DefinitionParameters
 *
 * @see parameters
 * return ParameterList
 */
fun parametersOf(vararg parameters: Any?) = ParametersHolder(parameters.toMutableList())

/**
 *
 */
fun emptyParametersHolder() = ParametersHolder()

/**
 * Help define a DefinitionParameters
 */
typealias ParametersDefinition = () -> ParametersHolder