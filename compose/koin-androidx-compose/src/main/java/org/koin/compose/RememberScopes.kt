@file:OptIn(KoinInternalApi::class)

package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope

@Composable
inline fun rememberKoinScope(scope: Scope): Scope {
    val koin = getKoin()
    val wrapper = remember(scope) {
        CompositionKoinScopeLoader(scope, koin)
    }
    return wrapper.scope
}