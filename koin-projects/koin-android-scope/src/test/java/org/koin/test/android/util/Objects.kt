package org.koin.test.android.util

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.ComponentCallbacks
import android.content.res.Configuration
import org.koin.android.scope.HasParentScope
import org.koin.core.scope.ScopeID

class Parent: LifecycleOwner, ComponentCallbacks {

    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        markState(Lifecycle.State.CREATED)
    }

    fun markState(state: Lifecycle.State) = lifecycleRegistry.markState(state)
    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    override fun onLowMemory() { }
    override fun onConfigurationChanged(newConfig: Configuration?) { }
}

class Child(override val parentScopeId: ScopeID): LifecycleOwner, ComponentCallbacks, HasParentScope {

    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        markState(Lifecycle.State.CREATED)
    }

    fun markState(state: Lifecycle.State) = lifecycleRegistry.markState(state)
    override fun getLifecycle(): Lifecycle = lifecycleRegistry
    override fun onLowMemory() { }
    override fun onConfigurationChanged(newConfig: Configuration?) { }
}