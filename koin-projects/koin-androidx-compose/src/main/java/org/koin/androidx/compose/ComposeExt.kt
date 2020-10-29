package org.koin.androidx.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Inject lazily a given dependency for [Composable] functions
 * @param qualifier
 * @param parameters
 *
 * @return Lazy instance of type T
 *
 * @author Henrique Horbovyi
 */

//@Composable
//inline fun <reified T> inject(
//        qualifier: Qualifier? = null,
//        noinline parameters: ParametersDefinition? = null
//): Lazy<T> = remember {
//    GlobalContext.get().inject(qualifier, parameters)
//}

@Composable
inline fun <reified T> get(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): T = remember {
    GlobalContext.get().get(qualifier, parameters)
}

@Composable
fun getKoin(): Koin = remember {
    GlobalContext.get()
}
