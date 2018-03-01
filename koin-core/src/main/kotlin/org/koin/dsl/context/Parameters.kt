package org.koin.dsl.context

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
}

/**
 * Parameters holder
 */
data class Parameters(private val values: Map<String, Any> = emptyMap()) : ParametersProvider {

    @Suppress("UNCHECKED_CAST")
    override fun <T> getOrNUll(key: String): T? {
        return values[key] as? T
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun <T> get(key: String): T = values[key] as T

}