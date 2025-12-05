package org.koin.compose.application

import androidx.compose.runtime.RememberObserver
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform

@KoinInternalApi
class CompositionKoinApplicationLoader(
    val koinApplication: KoinApplication? = null,
) : RememberObserver {

    var koin : Koin? = null

    init {
        start()
    }

    override fun onAbandoned() {
        koin?.logger?.warn("CompositionKoinApplicationLoader - onAbandoned - $this")
        stop()
    }

    override fun onForgotten() {
        koin?.logger?.debug("CompositionKoinApplicationLoader - onForgotten - $this")
        //don"t stop context, premature. Only de-allocate
        koin = null
    }

    override fun onRemembered() {
        start()
    }

    private fun start() {
        if (KoinPlatform.getKoinOrNull() == null && koinApplication != null){
            try {
                koin = startKoin(koinApplication).koin
                koin!!.logger.debug("$this -> attach Koin instance $koin")
            } catch (e: Exception) {
                error("Can't start Koin from Compose context - $e")
            }
        } else if (KoinPlatform.getKoinOrNull() != null) {
            koin = KoinPlatform.getKoin()
            koin!!.logger.debug("$this -> re-attach Koin instance $koin")
        } else {
            error("can't start Koin context, no koinApplication argument found nor existing context")
        }
    }

    private fun stop() {
        koin?.logger?.warn("CompositionKoinApplicationLoader - stop")
        koin = null
        stopKoin()
    }
}