package org.koin.compose

import androidx.compose.runtime.RememberObserver
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope

@KoinInternalApi
class CompositionKoinScopeLoader(
    val scope: Scope,
    val koin : Koin
) : RememberObserver {

    override fun onRemembered() {
        // Nothing to do
        koin.logger.debug("$this.onRemembered()")
    }

    override fun onForgotten() {
        koin.logger.debug("$this.onForgotten()")
        close()
    }

    override fun onAbandoned() {
        koin.logger.debug("$this.onAbandoned()")
        close()
    }

    private fun close() {
        koin.logger.debug("$this -> close scope id: '${scope.id}'")
        scope.close()
    }
}