package org.koin.androidx.scope.observer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import org.koin.core.scope.Scope

class LifecycleOnDestroyCloser(private val scope : Scope) : LifecycleObserver {

    init {
        scope.logger.debug("Observer on-destroy event to close $scope")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onStop() {
        scope.logger.debug("Got on-destroy event to close $scope")
        scope.close()
    }

}