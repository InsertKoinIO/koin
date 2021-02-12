package org.koin.androidx.scope

import org.koin.core.scope.Scope

/**
 * Android Component that can handle a Koin Scope
 */
interface AndroidScopeComponent {
    val scope: Scope
}