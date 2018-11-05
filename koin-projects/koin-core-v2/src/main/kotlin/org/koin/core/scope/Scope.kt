package org.koin.core.scope

import org.koin.core.Koin
import java.util.*

data class Scope internal constructor(val id: String, internal val internalId: String = UUID.randomUUID().toString()) {

    internal var koin: Koin? = null

    /**
     * Is Scope associated to Koin
     */
    fun isRegistered() = koin != null

    fun close() {
        if (koin == null) error("Can't close Scope $id without any Koin instance associated")
        koin?.closeScope(internalId)
    }

    fun register(koin: Koin) {
        this.koin = koin
    }
}