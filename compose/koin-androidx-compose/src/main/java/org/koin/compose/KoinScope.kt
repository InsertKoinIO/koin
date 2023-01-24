package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope

@OptIn(KoinInternalApi::class)
@PublishedApi
internal val KoinScope = compositionLocalOf {
    GlobalContext.get().scopeRegistry.rootScope
}

@Composable
fun KoinScope(
    scopeDefinition: Koin.() -> Scope,
    content: @Composable () -> Unit
) {
    val koin = getKoin()
    val scope = scopeDefinition(koin)
    CompositionLocalProvider(
        KoinScope provides scope,
    ) {
        content()
    }
}