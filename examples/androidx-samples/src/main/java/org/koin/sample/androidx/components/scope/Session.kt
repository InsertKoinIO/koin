package org.koin.sample.androidx.components.scope

import org.koin.sample.androidx.scope.ScopedActivityA
import java.util.UUID

class Session{
    var id: String = UUID.randomUUID().toString()
}

data class SessionActivity(val activity: ScopedActivityA, val id: String = UUID.randomUUID().toString())
