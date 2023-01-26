package org.koin.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.koin.core.Koin
import org.koin.core.component.getScopeId
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

/**
 * Create Koin Scope & close it when Composition is on onForgotten/onAbandoned
 *
 * @param scopeDefinition - lambda to define scope
 *
 * @see rememberKoinScope
 *
 * @author Arnaud Giuliani
 */
@Composable
fun KoinScope(
    scopeDefinition: Koin.() -> Scope,
    content: @Composable () -> Unit
) {
    val scope = scopeDefinition(getKoin())
    RememberScope(scope, content)
}

/**
 * Create Koin Scope from type T & close it when Composition is on onForgotten/onAbandoned
 *
 * @param scopeID
 *
 * @see rememberKoinScope
 *
 * @author Arnaud Giuliani
 */
@Composable
inline fun <reified T : Any> KoinScope(
    scopeID: ScopeID,
    noinline content: @Composable () -> Unit
) {
    val scope = getKoin().getOrCreateScope<T>(scopeID)
    RememberScope(scope, content)
}

@Composable
@PublishedApi
internal fun RememberScope(scope: Scope, content: @Composable () -> Unit) {
    rememberKoinScope(scope)
    CompositionLocalProvider(
        LocalKoinScope provides scope,
    ) {
        content()
    }
}

/**
 * Create Koin Scope from context & close it when Composition is on onForgotten/onAbandoned
 *
 * @param context
 *
 * @see rememberKoinScope
 *
 * @author Arnaud Giuliani
 */
@Composable
inline fun <reified T : Any> KoinScope(
    context : Any,
    noinline content: @Composable () -> Unit
) {
    val scope = getKoin().getOrCreateScope<T>(context.getScopeId())
    RememberScope(scope, content)
}

/**
 * Create Koin Scope from type T & close it when Composition is on onForgotten/onAbandoned
 *
 * @param scopeID
 * @param scopeQualifier
 *
 * @see rememberKoinScope
 *
 * @author Arnaud Giuliani
 */
@Composable
inline fun KoinScope(
    scopeID: ScopeID,
    scopeQualifier: Qualifier,
    noinline content: @Composable () -> Unit
) {
    val scope = getKoin().getOrCreateScope(scopeID, scopeQualifier)
    RememberScope(scope, content)
}