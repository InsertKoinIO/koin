package org.koin.core.scope


/**
 * ModuleDefinition callback
 */
interface ScopeCallbacks {

    /**
     * Notify on context release
     * @param path - context name
     */
    fun onScopeReleased(path: String)
}
