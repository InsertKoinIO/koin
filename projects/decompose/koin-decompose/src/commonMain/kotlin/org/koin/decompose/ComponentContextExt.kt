package org.koin.decompose

import com.arkivanov.essenty.lifecycle.doOnDestroy
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeCallback

/**
 * Create Scope for Component.
 * Doesn't link to parent component's Scope
 */
fun DecomposeScopeComponent.createComponentScope() =
    getKoin().getScopeOrNull(getScopeId()) ?: createScopeForCurrentLifecycle()

/**
 * Provide scope tied to Component
 */
fun DecomposeScopeComponent.componentScope() = lazy { createComponentScope() }

private fun DecomposeScopeComponent.createScopeForCurrentLifecycle(): Scope {
    val scope = getKoin().createScope(getScopeId(), getScopeName(), this)
    scope.registerCallback(
        object : ScopeCallback {
            override fun onScopeClose(scope: Scope) = this@createScopeForCurrentLifecycle.onScopeClose(scope)
        },
    )
    lifecycle.doOnDestroy {
        if (scope.isNotClosed()) {
            scope.close()
        }
    }
    return scope
}