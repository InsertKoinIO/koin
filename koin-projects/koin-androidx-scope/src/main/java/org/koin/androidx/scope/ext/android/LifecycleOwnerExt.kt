package org.koin.androidx.scope.ext.android

import androidx.lifecycle.LifecycleOwner
import org.koin.androidx.scope.ScopeObserver


/**
 * Set a Scope Observer onto the actual LifecycleOwner component
 * @see ScopeObserver
 *
 * @param module : module names
 */
fun LifecycleOwner.scope(vararg module: String) {
    lifecycle.addObserver(ScopeObserver(this.javaClass.canonicalName, module))
}
