package org.koin.android.architecture.ext.android

import android.arch.lifecycle.LifecycleOwner
import org.koin.android.architectre.ScopeObserver

/**
 * Set a Scope Observer onto the actual LifecycleOwner component
 * @see ScopeObserver
 *
 * @param module : module names
 */
fun LifecycleOwner.scope(vararg module: String) {
    lifecycle.addObserver(ScopeObserver(this.javaClass.canonicalName, module))
}
