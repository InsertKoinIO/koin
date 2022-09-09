package org.koin.sample.androidx.components.scope

import java.util.*

class Session {
    var id: String = UUID.randomUUID().toString()
}

class SessionActivity {
    val id: String = UUID.randomUUID().toString()
}
