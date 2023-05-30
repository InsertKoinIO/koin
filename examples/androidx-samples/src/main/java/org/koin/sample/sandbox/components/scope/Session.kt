package org.koin.sample.sandbox.components.scope

import java.util.*

class Session {
    var id: String = UUID.randomUUID().toString()
}

class SessionActivity {
    val id: String = UUID.randomUUID().toString()
}
