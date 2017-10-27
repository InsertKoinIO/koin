package org.koin.android.logger

import android.util.Log
import org.koin.log.Logger

/**
 * Logger that uses Android Log.i
 *
 * @author Arnaud Giuliani
 */
class AndroidLogger : Logger {
    override fun log(msg: String) {
        Log.i("KOIN", msg)
    }
}