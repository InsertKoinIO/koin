package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T> koinInject(
    qualifier: Qualifier? = null,
    scope: Scope = getKoin().scopeRegistry.rootScope,
    noinline parameters: ParametersDefinition? = null,
): T = scope.get(qualifier, parameters)

@OptIn(KoinInternalApi::class)
@Composable
inline fun <reified T> rememberKoinInject(
    qualifier: Qualifier? = null,
    scope: Scope = getKoin().scopeRegistry.rootScope,
    noinline parameters: ParametersDefinition? = null,
): T = remember(qualifier, scope, parameters) {
    scope.get(qualifier, parameters)
}


