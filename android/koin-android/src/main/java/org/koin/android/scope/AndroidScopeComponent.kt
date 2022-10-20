package org.koin.android.scope

import org.koin.core.scope.Scope

/**
 * Android Component that can handle a Koin Scope
 */
//TODO Breaking Changes to make it 'var scope: Scope?'
interface AndroidScopeComponent {
    // TODO Change val to other type of value
    // TODO Make it nullable
    val scope: Scope
}