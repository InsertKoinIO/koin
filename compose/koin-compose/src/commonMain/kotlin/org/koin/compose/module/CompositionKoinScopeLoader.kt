package org.koin.compose.module

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
    }

    override fun onForgotten() {
        close()
    }

    override fun onAbandoned() {
        close()
    }

    private fun close() {
        koin.logger.debug("$this -> close scope id: '${scope.id}'")
        scope.close()
    }
}