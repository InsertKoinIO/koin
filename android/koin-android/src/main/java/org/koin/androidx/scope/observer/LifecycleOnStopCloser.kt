package org.koin.androidx.scope.observer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import org.koin.core.scope.Scope

class LifecycleOnStopCloser(private val scope : Scope) : LifecycleObserver {

    init {
        scope.logger.debug("Observer on-stop event to close $scope")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        scope.logger.debug("Got on-stop event to close $scope")
        scope.close()
    }

}