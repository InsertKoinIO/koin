package org.koin.decompose

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope

interface DecomposeScopeComponent : KoinScopeComponent, ComponentContext {
    fun onScopeClose(scope: Scope) { // Invokes when the scope is closing
    }
}