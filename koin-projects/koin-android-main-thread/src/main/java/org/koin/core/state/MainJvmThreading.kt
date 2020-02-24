package org.koin.core.state

import android.os.Looper

class MainJvmThreading : JvmThreading{
    override fun assertStateThread() {
        if(Looper.getMainLooper() !== Looper.myLooper())
            throw IllegalStateException("Must be main thread")
    }

    override fun <R> mpsynchronized(lock: Any, block: () -> R): R {
        assertStateThread()
        return block()
    }
}