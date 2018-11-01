package org.koin.core

object StandAlone {
    internal var koin: Koin? = null

    fun getKoin(): Koin = koin ?: error("Koin has not been initialized")
}