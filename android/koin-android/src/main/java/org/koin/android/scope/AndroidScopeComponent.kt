package org.koin.android.scope

import org.koin.core.scope.Scope

/**
 * Android Component that can handle a Koin Scope
 */
interface AndroidScopeComponent {
    var scope: Scope?
    fun requireScope() : Scope = scope ?: error("Trying to access Android Scope on '$this' but scope is not created")
}