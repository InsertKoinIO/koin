package org.koin.android.logger

import android.util.Log
import org.koin.log.Logger

/**
 * Logger that uses Android Log.i
 *
 * @author Arnaud Giuliani
 */
class AndroidLogger : Logger {
    val TAG = "KOIN"

    override fun debug(msg: String) {
        Log.d(TAG, msg)
    }

    override fun err(msg: String) {
        Log.e(TAG, "[ERROR] - $msg")
    }

    override fun log(msg: String) {
        Log.i(TAG, msg)
    }
}