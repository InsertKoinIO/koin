package org.koin.android.architectre

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import org.koin.core.Koin
import org.koin.standalone.KoinComponent
import org.koin.standalone.release

/**
 * Observe a LifecycleOwner
 *
 * on ON_DESTROY drop given module path
 */
class ScopeObserver(val className: String, val module: Array<out String>) : LifecycleObserver, KoinComponent {

    /**
     * Handle ON_DESTROY to release Koin modules
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Koin.logger.log("received ON_DESTROY for $className")
        module.forEach { release(it) }
    }
}