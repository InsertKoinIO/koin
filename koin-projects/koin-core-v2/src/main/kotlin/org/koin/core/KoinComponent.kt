package org.koin.core

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.standalone.StandAloneKoinApplication

interface KoinComponent {
    fun getKoin() = StandAloneKoinApplication.get().koin
}

inline fun <reified T> KoinComponent.get(name: String? = null, noinline parameters: ParametersDefinition? = null): T =
    getKoin().get<T>(name, parameters)

inline fun <reified T> KoinComponent.inject(
    name: String? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> =
    getKoin().inject(name, parameters)