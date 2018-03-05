package org.koin.dsl.context

import org.koin.core.parameter.ParameterMap
import org.koin.error.MissingParameterException

/**
 * Provide a parameter
 */
interface ParametersProvider {

    /**
     * Returns the value corresponding to the given [key] - cast to type T
     */
    operator fun <T> get(key: String): T

    /**
     * Returns the value or null if key is missing, corresponding to the given [key] - cast to type T
     */
    fun <T> getOrNUll(key: String): T?

    val values: ParameterMap
}

/**
 * Parameters holder
 */
data class Parameters(override inline val values: ParameterMap = { emptyMap() }) : ParametersProvider {

    private val unfoldValues by lazy { values() }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getOrNUll(key: String): T? {
        return unfoldValues[key] as? T
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun <T> get(key: String): T =
        if (!unfoldValues.containsKey(key)) throw MissingParameterException("Parameter '$key' is missing")
        else unfoldValues[key] as T

}