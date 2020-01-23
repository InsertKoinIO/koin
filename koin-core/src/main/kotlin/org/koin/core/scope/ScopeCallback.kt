package org.koin.core.scope

/**
 * Scope Callback
 */
interface ScopeCallback {

    /**
     * Called when scope is closing
     * @param scope
     */
    fun onScopeClose(scope: Scope)
}
