package org.koin.core.scope

/**
 * Scope Callback
 */
interface ScopeCallback {

    /**
     * Called when scope is closing
     */
    fun onScopeClose()
}