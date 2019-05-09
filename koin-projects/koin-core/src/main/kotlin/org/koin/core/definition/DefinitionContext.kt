// package org.koin.core.definition
//
// import org.koin.core.Koin
// import org.koin.core.error.MissingPropertyException
// import org.koin.core.parameter.ParametersDefinition
// import org.koin.core.qualifier.Qualifier
// import org.koin.core.scope.Scope
//
// class DefinitionContext(val koin: Koin, val scope: Scope = Scope.GLOBAL) {
//
//    /**
//     * Get the current Scope
//     */
//    fun currentScope(): Scope = scope
//
//    /**
//     * Resolve an instance from Koin
//     * @param qualifier
//     * @param parameters
//     */
//    inline fun <reified T> get(
//        qualifier: Qualifier? = null,
//        noinline parameters: ParametersDefinition? = null
//    ): T {
//        return koin.get(qualifier, currentScope(), parameters)
//    }
//
//    /**
//     * Resolve an instance from Koin / extenral scope instance
//     * @param qualifier
//     * @param scope
//     * @param parameters
//     */
//    inline fun <reified T> get(
//        qualifier: Qualifier? = null,
//        scope: Scope,
//        noinline parameters: ParametersDefinition? = null
//    ): T {
//        return koin.get(qualifier, scope, parameters)
//    }
//
//    /**
//     * Retrieve from SCope
//     */
//    inline fun <reified T> getFromScope(
//        scopeId: String,
//        qualifier: Qualifier? = null,
//        noinline parameters: ParametersDefinition? = null
//    ): T {
//        return koin.get(qualifier, koin.getScope(scopeId), parameters)
//    }
//
//    /**
//     * Get a property from Koin
//     * @param key
//     * @param defaultValue
//     */
//    fun <T> getProperty(key: String, defaultValue: T? = null): T {
//        return koin.getProperty(key, defaultValue)
//                ?: throw MissingPropertyException("Property '$key' is missing")
//    }
// }