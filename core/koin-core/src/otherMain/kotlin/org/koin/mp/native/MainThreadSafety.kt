// package org.koin.mp.native
//
// actual val isMainThread: Boolean
//    get() {
//        return try {
//            //Trying to access a global val on a background thread will throw an exception
//            dummyData
//            true
//        } catch (t: Throwable) {
//            false
//        }
//    }
//
// private data class DummyData(val s: String)
//
// private val dummyData = DummyData("arst")
