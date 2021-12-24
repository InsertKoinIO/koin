package org.koin.mp.native

import kotlinx.cinterop.StableRef
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze

@OptIn(ExperimentalStdlibApi::class)
internal fun <T : Any> createdGuardedValue(startVal: T):GuardedValue<T>{
    return if(isExperimentalMM()){
        AnyThreadValue(startVal)
    }else{
        MainThreadValue(startVal)
    }
}

internal interface GuardedValue<T : Any> {
    fun get(): T
}

internal class MainThreadValue<T : Any>(startVal: T) :GuardedValue<T>{
    private val stableRef = StableRef.create(startVal)

    init {
        assertMainThread()
        startVal.ensureNeverFrozen()
        freeze()
    }

    override fun get(): T {
        assertMainThread()
        return stableRef.get()
    }
}

internal class AnyThreadValue<T : Any>(private val startVal: T): GuardedValue<T>{
    init {
        startVal.ensureNeverFrozen()
        ensureNeverFrozen()
    }
    override fun get(): T = startVal
}

internal expect val isMainThread: Boolean// = NSThread.isMainThread

internal fun assertMainThread() {
    if (!isMainThread) throw IllegalStateException("Must be main thread")
}