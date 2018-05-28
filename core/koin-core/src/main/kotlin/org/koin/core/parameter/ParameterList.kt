package org.koin.core.parameter

/**
 * ParameterList - List of parameter
 * Usable with exploded declaration
 *
 * @author - Arnaud GIULIANI
 * @author - Laurent BARESSE
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

