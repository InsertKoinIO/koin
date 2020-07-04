package org.koin.androidx.compose

import androidx.compose.Composable
import androidx.compose.remember
import org.koin.core.context.KoinContextHandler
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * @author Henrique Horbovyi
 * */

/**
 * Inject lazily a given dependency for [Composable] functions
 * @param qualifier
 * @param parameters
 *
 * @return Lazy instance of type T
 */

@Composable
inline fun <reified T> inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> = remember {
    val context = KoinContextHandler.get()
    context.inject(qualifier, parameters)
}
