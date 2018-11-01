package org.koin.core.standalone

import org.koin.core.Koin

object StandAloneContext {
    internal var koin: Koin? = null

    fun getKoin(): Koin = koin ?: error("Koin has not been initialized")
}