package org.koin.core.parameter


        /**
         * Parameter type to be passsed to BeanDefinition
         */
typealias ParameterMap = () -> Map<String, Any>

/**
 * helper to write map of Values into ParameterMap
 */
fun valuesOf(pair: Pair<String, Any>): () -> Map<String,Any> =
    { mapOf(pair) }