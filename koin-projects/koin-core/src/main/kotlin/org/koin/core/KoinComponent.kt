package org.koin.core

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.scope.Scope
import org.koin.core.standalone.StandAloneKoinApplication

interface KoinComponent {
    fun getKoin() = StandAloneKoinApplication.get().koin
}

inline fun <reified T> KoinComponent.get(
    name: String? = null,
    scope: Scope? = null,
    noinline parameters: ParametersDefinition? = null
): T =
    getKoin().get(name, scope, parameters)

inline fun <reified T> KoinComponent.inject(
    name: String? = null,
    scope: Scope? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> =
    getKoin().inject(name, scope, parameters)