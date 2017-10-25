package org.koin.android.logger

import android.util.Log
import org.koin.log.Logger

class AndroidLogger : Logger {
    override fun log(msg: String) {
        Log.i("KOIN", msg)
    }
}