//package org.koin.mp.native
//
//import kotlinx.cinterop.StableRef
//import kotlin.native.concurrent.ensureNeverFrozen
//import kotlin.native.concurrent.freeze
//
//class MainThreadValue<T : Any>(startVal: T) {
//    private val stableRef = StableRef.create(startVal)
//
//    init {
//        assertMainThread()
//        startVal.ensureNeverFrozen()
//        freeze()
//    }
//
//    fun get(): T {
//        assertMainThread()
//        return stableRef.get()
//    }
//}
//
//expect val isMainThread: Boolean// = NSThread.isMainThread
//
//internal fun assertMainThread() {
//    if (!isMainThread) throw IllegalStateException("Must be main thread")
//}