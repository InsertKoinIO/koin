package org.koin.sample.sandbox.components.scope

import java.util.*

class Session {
    var id: String = UUID.randomUUID().toString()
}

class SessionConsumer(private val session: Session){

    fun getSessionId() = session.id

}

class SessionActivity {
    val id: String = UUID.randomUUID().toString()
}
