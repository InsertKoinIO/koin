package org.koin.core.scope

import java.util.*

class Scope(val id: String) {

    internal val internalId: String = UUID.randomUUID().toString()

}