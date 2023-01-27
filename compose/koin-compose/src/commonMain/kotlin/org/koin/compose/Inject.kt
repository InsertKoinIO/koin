package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

/**
 * Resolve Koin dependency
 *
 * @param qualifier
 * @param scope - Koin's root default
 * @param parameters - injected parameters
 *
 * @author Arnaud Giuliani
 */
@Composable
inline fun <reified T> koinInject(
    qualifier: Qualifier? = null,
    scope: Scope = LocalKoinScope.current,
    noinline parameters: ParametersDefinition? = null,
): T = rememberKoinInject(qualifier, scope, parameters)

/**
 * alias of koinInject()
 *
 * @see koinInject
 *
 * @author Arnaud Giuliani
 */
@Composable
inline fun <reified T> rememberKoinInject(
    qualifier: Qualifier? = null,
    scope: Scope = LocalKoinScope.current,
    noinline parameters: ParametersDefinition? = null,
): T = remember(qualifier, scope, parameters) {
    scope.get(qualifier, parameters)
}


