package org.koin.core.parameter

import org.koin.core.error.NoParameterFoundException
import org.koin.core.parameter.ParametersHolder.Companion.MAX_PARAMS

/**
 * ParametersHolder - Parameter holder
 * Usable with exploded declaration
 *
 * @author - Arnaud GIULIANI
 */
@Suppress("UNCHECKED_CAST")
class ParametersHolder internal constructor(vararg val values: Any?) {

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
     * Get first element of given type T
     * return T
     */
    inline fun <reified T> get() = values.first { it is T }

    companion object {
        const val MAX_PARAMS = 5
    }
}

/**
 * Build a ParametersHolder
 *
 * @see parameters
 * return ParameterList
 */
fun parametersOf(vararg parameters: Any?) =
    if (parameters.size <= MAX_PARAMS) ParametersHolder(*parameters) else error("Can't build ParametersHolder for more than $MAX_PARAMS arguments")