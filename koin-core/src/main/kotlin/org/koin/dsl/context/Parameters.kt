package org.koin.dsl.context

/**
 * Provide a parameter
 */
interface ParametersProvider {
    operator fun <T> get(key: String): T
}

/**
 * Parameters holder
 */
data class Parameters(private val values: Map<String, Any> = emptyMap()) : ParametersProvider {

    /**
     * Returns the value corresponding to the given [key] - cast to type T
     */
    @Suppress("UNCHECKED_CAST")
    override operator fun <T> get(key: String): T = values[key] as T
}