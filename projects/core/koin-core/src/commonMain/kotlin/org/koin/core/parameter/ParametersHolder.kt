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
package org.koin.core.parameter

import org.koin.core.error.DefinitionParameterException
import org.koin.core.error.NoParameterFoundException
import org.koin.core.module.KoinDslMarker
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * DefinitionParameters - Parameter holder
 * Usable with exploded declaration
 *
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
@KoinDslMarker
open class ParametersHolder(
    @PublishedApi
    internal val _values: MutableList<Any?> = mutableListOf(),
    // TODO by default useIndexedValues to null, to keep compatibility with both indexed params & set params
    val useIndexedValues: Boolean? = null,
) {

    val values: List<Any?> get() = _values

    open fun <T> elementAt(i: Int, clazz: KClass<*>): T =
        if (i < _values.size) {
            _values[i] as T
        } else {
            throw NoParameterFoundException(
                "Can't get injected parameter #$i from $this for type '${clazz.getFullName()}'",
            )
        }

    inline operator fun <reified T> component1(): T = elementAt(0, T::class)
    inline operator fun <reified T> component2(): T = elementAt(1, T::class)
    inline operator fun <reified T> component3(): T = elementAt(2, T::class)
    inline operator fun <reified T> component4(): T = elementAt(3, T::class)
    inline operator fun <reified T> component5(): T = elementAt(4, T::class)

    var index: Int = 0

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
    fun isEmpty() = _values.isEmpty()

    /**
     * Tells if it has parameters
     */
    fun isNotEmpty() = _values.isNotEmpty()

    fun insert(index: Int, value: Any): ParametersHolder {
        _values.add(index, value)
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
    inline fun <reified T : Any> get(): T =
        getOrNull(T::class) ?: throw DefinitionParameterException("No value found for type '${T::class.getFullName()}'")

    /**
     * Get first element of given type T
     * return T
     */
    inline fun <reified T : Any> getOrNull(): T? = getOrNull(T::class)

    /**
     * Get first element of given type T
     * return T
     */
    open fun <T> getOrNull(clazz: KClass<*>): T? {
        return if (_values.isEmpty()) {
            null
        } else {
            when (useIndexedValues) {
                null -> getIndexedValue<T>(clazz) ?: getFirstValue<T>(clazz)
                true -> getIndexedValue<T>(clazz)
                else -> getFirstValue<T>(clazz)
            }
        }
    }

    private fun <T> getFirstValue(clazz: KClass<*>): T? {
        return _values.firstOrNull { clazz.isInstance(it) } as? T
    }

    private fun <T> getIndexedValue(clazz: KClass<*>): T? {
        val currentValue: T? = _values[index].takeIf { clazz.isInstance(it) } as? T
        if (currentValue != null) {
            increaseIndex()
        }
        return currentValue
    }

    @PublishedApi
    internal fun increaseIndex() {
        if (index < _values.lastIndex) {
            index += 1
        }
    }

    override fun toString(): String = "DefinitionParameters${_values.toList()}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParametersHolder) return false

        return values == other.values && useIndexedValues == other.useIndexedValues
    }

    override fun hashCode(): Int {
        return 31 * values.hashCode() + (useIndexedValues?.hashCode() ?: 0)
    }
}

/**
 * Build a DefinitionParameters
 *
 * Use indexed value or fallback to first value of given type if needed
 *
 * @see parameters
 * return ParameterList
 */
fun parametersOf(vararg parameters: Any?) = ParametersHolder(parameters.toMutableList())

/**
 * DefinitionParameters with array of values, dedicated to be consumed one by one
 *
 * Ideally used for cascading parameter, or if you have several parameters of same type
 *
 * @see parameters
 * return ParameterList
 */
fun parameterArrayOf(vararg parameters: Any?) = ParametersHolder(parameters.toMutableList(), useIndexedValues = true)

/**
 * DefinitionParameters with set of values
 *
 * Used to pass different types of values
 *
 * @see parameters
 * return ParameterList
 */
fun parameterSetOf(vararg parameters: Any?) = ParametersHolder(parameters.toMutableList(), useIndexedValues = false)

/**
 *
 */
fun emptyParametersHolder() = ParametersHolder()

/**
 * Help define a DefinitionParameters
 */
typealias ParametersDefinition = () -> ParametersHolder
