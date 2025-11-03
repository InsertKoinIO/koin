package org.koin.compose

import org.koin.core.annotation.KoinInternalApi

/**
 * Compose Context Wrapper for Koin & Scope
 *
 * Internal wrapper used by LocalKoinApplicationContext and LocalKoinScopeContext to handle:
 * - Lazy initialization of Koin context and scopes
 * - Recovery from closed scopes by resetting to default values
 * - Context restoration after errors or configuration changes
 *
 * This wrapper is necessary to handle the Compose lifecycle where scopes may be closed
 * but the composition local still exists, allowing for graceful fallback to default contexts.
 *
 * @param initValue Initial value for the wrapper
 * @param setValue Lambda to reset/reinitialize the value when needed
 *
 * @author Arnaud Giuliani
 */
@KoinInternalApi
class ComposeContextWrapper<T>(initValue : T? = null, val setValue : (() -> T)? = null){

    private var _value : T? = initValue

    /**
     * Resets the wrapped value by invoking setValue lambda.
     * Used when recovering from closed scopes or errors.
     */
    fun resetValue(): T? {
        _value = setValue?.invoke()
        return _value
    }

    /**
     * Gets the wrapped value, lazily initializing it if needed.
     * @throws IllegalStateException if value cannot be retrieved or initialized
     */
    fun getValue() : T {
        if (_value == null){
            _value = setValue?.invoke()
        }
        return _value ?: error("Can't retrieve Koin context value. Ensure Koin is properly initialized with startKoin() or KoinApplication.")
    }
}