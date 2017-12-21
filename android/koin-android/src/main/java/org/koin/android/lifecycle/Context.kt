package org.koin.android.lifecycle

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.koin.standalone.KoinComponent
import org.koin.standalone.releaseContext

sealed class LifecycleEvent {
    object OnDestroy : LifecycleEvent()
    object OnStop : LifecycleEvent()
    object OnPause : LifecycleEvent()
}

fun FragmentActivity.manageContext(
        contextName: String,
        releaseEvent: LifecycleEvent = LifecycleEvent.OnDestroy
): LifecycleObserver = LifecycleContextBinder(contextName, releaseEvent, lifecycle)

fun Fragment.manageContext(
        contextName: String,
        releaseEvent: LifecycleEvent = LifecycleEvent.OnDestroy
): LifecycleObserver = LifecycleContextBinder(contextName, releaseEvent, lifecycle)

private class LifecycleContextBinder(
        private val contextName: String,
        private val boundEvent: LifecycleEvent,
        lifecycle: Lifecycle
) : LifecycleObserver, KoinComponent {
    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        when (boundEvent) {
            LifecycleEvent.OnDestroy -> releaseContext(contextName)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        when (boundEvent) {
            LifecycleEvent.OnStop -> releaseContext(contextName)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
        when (boundEvent) {
            LifecycleEvent.OnPause -> releaseContext(contextName)
        }
    }
}

