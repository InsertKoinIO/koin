package org.koin.core.scope

import org.koin.core.Koin

data class Scope(val id: String) {

//    internal val internalId: String = UUID.randomUUID().toString()

    lateinit var koin: Koin

    fun close() {
        TODO()
    }


}