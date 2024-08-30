package org.koin.compose.application

import androidx.compose.runtime.RememberObserver
import org.koin.core.KoinApplication
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform

@KoinInternalApi
class CompositionKoinApplicationLoader(
    val koinApplication: KoinApplication,
) : RememberObserver {

    override fun onAbandoned() {
        stop()
    }

    override fun onForgotten() {
        stop()
    }

    override fun onRemembered() {
        start()
    }

    private fun start() {
        val koin = startKoin(koinApplication).koin
        koin.logger.debug("$this -> started Koin Application $koinApplication")
    }

    private fun stop() {
        KoinPlatform.getKoinOrNull()?.logger?.debug("$this -> stop Koin Application $koinApplication")
        stopKoin()
    }
}