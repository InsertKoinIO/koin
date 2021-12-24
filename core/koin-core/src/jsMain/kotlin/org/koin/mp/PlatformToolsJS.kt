package org.koin.mp

import org.koin.core.context.GlobalContext
import org.koin.core.context.KoinContext
import org.koin.core.logger.*
import kotlin.random.Random
import kotlin.reflect.KClass

//actual object PlatformTools {
//    actual fun getClassName(kClass: KClass<*>): String {
//        return kClass.simpleName ?: "KClass@${hashCode()}"
//    }
//
//    actual fun printStackTrace(throwable: Throwable) {
//        throwable.printStackTrace()
//    }
//
//    actual fun stackTrace(): List<String> = Exception().toString().split("\n")
//
//    actual fun printLog(level: Level, msg: MESSAGE) {
//        println("[$level] $KOIN_TAG $msg")
//    }
//}
//internal actual fun Any.ensureNeverFrozen() {}
//internal actual fun <R> mpsynchronized(lock: Any, block: () -> R): R = block()

actual object KoinPlatformTools {
    actual fun getStackTrace(e: Exception): String = e.toString() + Exception().toString().split("\n")
    actual fun getClassName(kClass: KClass<*>): String = kClass.simpleName ?: "KClass@${kClass.hashCode()}"
    // TODO Better Id generation?
    actual fun generateId(): String = Random.nextDouble().hashCode().toString()
    actual fun defaultLazyMode(): LazyThreadSafetyMode = LazyThreadSafetyMode.NONE
    actual fun defaultLogger(level: Level): Logger = PrintLogger(level)
    actual fun defaultContext(): KoinContext = GlobalContext
    actual fun <R> synchronized(lock: SyncCapable, block: () -> R) = block()
    actual fun <K, V> safeHashMap(): MutableMap<K, V> = HashMap()
}

actual typealias SyncCapable = Any