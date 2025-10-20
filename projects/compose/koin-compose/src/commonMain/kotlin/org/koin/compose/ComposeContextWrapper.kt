package org.koin.compose

import org.koin.core.annotation.KoinInternalApi

/**
 * Compose Context Wrapper for Koin & Scope
 */
//TODO Check for cleanup process if needed
@KoinInternalApi
class ComposeContextWrapper<T>(initValue : T? = null, val setValue : (() -> T)? = null){

    private var _value : T? = initValue

    fun resetValue(): T? {
        _value = setValue?.invoke()
        return _value
    }

    fun getValue() : T {
        if (_value == null){
            _value = setValue?.invoke()
        }
        return _value ?: error("Can't retrieve value for ")
    }
}