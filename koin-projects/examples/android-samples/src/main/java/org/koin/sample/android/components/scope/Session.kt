package org.koin.sample.android.components.scope

import android.app.Activity
import java.util.UUID

data class Session(var id: String = UUID.randomUUID().toString())
data class SessionActivity(val activity: Activity, var id: String = UUID.randomUUID().toString())