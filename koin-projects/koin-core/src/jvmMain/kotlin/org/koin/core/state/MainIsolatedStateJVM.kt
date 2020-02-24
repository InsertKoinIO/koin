package org.koin.core.state

internal actual class MainIsolatedState<T:Any> actual constructor(private val startVal: T) {
    init {
        jvmThreading.assertStateThread()
    }

    actual fun _get(): T {
        jvmThreading.assertStateThread()
        return startVal
    }
}

internal val jvmThreading:JvmThreading by lazy {
    try {
        val cl = Class.forName("org.koin.core.state.MainJvmThreading")
        cl.newInstance() as JvmThreading
    } catch (e: Exception) {
        DefaultJvmThreading()
    }
}

interface JvmThreading {
    fun assertStateThread()
    fun <R> mpsynchronized(lock: Any, block: () -> R): R
}

internal class DefaultJvmThreading : JvmThreading{
    override fun assertStateThread() {
        //Nothing
    }

    override fun <R> mpsynchronized(lock: Any, block: () -> R): R = synchronized(lock) {
        block()
    }
}