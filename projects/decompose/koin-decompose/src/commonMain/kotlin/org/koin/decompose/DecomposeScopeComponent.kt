package org.koin.decompose

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope

/**
 * Decompose Component that can handle a Koin Scope
 */
interface DecomposeScopeComponent : KoinScopeComponent, ComponentContext {

    /**
     * Called before closing a scope, on onDestroy
     */
    fun onScopeClose(scope: Scope) {}
}