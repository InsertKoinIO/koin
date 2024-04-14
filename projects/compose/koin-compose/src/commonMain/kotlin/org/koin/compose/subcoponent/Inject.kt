package org.koin.compose.subcoponent

import androidx.compose.runtime.Composable
import org.koin.compose.LocalKoinScope
import org.koin.compose.koinInject
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Obtains and remembers instance of the given type from the current scope
 *
 * @param qualifier the qualifier to be used
 * @param parameters definition of the parameters to be used
 *
 * @author Antonio Vicente
 */
@Composable
inline fun <reified T> inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T = koinInject<T>(
    qualifier = qualifier,
    scope = LocalKoinScope.current,
    parameters = parameters,
)




