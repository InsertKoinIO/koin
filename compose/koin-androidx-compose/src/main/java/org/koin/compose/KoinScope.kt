package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.getScopeId
import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

@OptIn(KoinInternalApi::class)
val LocalKoinScope = compositionLocalOf {
    GlobalContext.get().scopeRegistry.rootScope
}

@Composable
fun KoinScope(
    scopeDefinition: Koin.() -> Scope,
    content: @Composable () -> Unit
) {
    val scope = scopeDefinition(getKoin())
    rememberScope(scope, content)
}

@Composable
inline fun <reified T : Any> KoinScope(
    scopeID: ScopeID,
    noinline content: @Composable () -> Unit
) {
    val scope = getKoin().getOrCreateScope<T>(scopeID)
    rememberScope(scope, content)
}

@Composable
@PublishedApi
internal fun rememberScope(scope: Scope, content: @Composable () -> Unit) {
    rememberKoinScope(scope)
    CompositionLocalProvider(
        LocalKoinScope provides scope,
    ) {
        content()
    }
}

@Composable
inline fun <reified T : Any> KoinScope(
    context : Any,
    noinline content: @Composable () -> Unit
) {
    val scope = getKoin().getOrCreateScope<T>(context.getScopeId())
    rememberScope(scope, content)
}

@Composable
inline fun KoinScope(
    scopeID: ScopeID,
    scopeQualifier: Qualifier,
    noinline content: @Composable () -> Unit
) {
    //TODO Keep GetOrCreate or just create?
    val scope = getKoin().getOrCreateScope(scopeID, scopeQualifier)
    rememberScope(scope, content)
}