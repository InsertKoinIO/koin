@file:OptIn(KoinInternalApi::class)

package org.koin.compose.scope

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.getKoin
import org.koin.compose.module.CompositionKoinScopeLoader
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope

/**
 * Remember Koin Scope & run CompositionKoinScopeLoader to handle scope closure
 *
 * @author Arnaud Giuliani
 */
@Composable
inline fun rememberKoinScope(scope: Scope): Scope {
    val koin = getKoin()
    val wrapper = remember(scope) {
        CompositionKoinScopeLoader(scope, koin)
    }
    return wrapper.scope
}