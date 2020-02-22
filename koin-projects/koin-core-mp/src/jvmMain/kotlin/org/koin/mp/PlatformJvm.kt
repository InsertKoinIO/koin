package org.koin.mp

import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import kotlin.reflect.KClass

actual object PlatformTools {
    actual fun className(kClass: KClass<*>): String = kClass.java.simpleName
    actual fun printStackTrace(throwable: Throwable) {
        throwable.printStackTrace()
    }

    actual fun stackTrace(): List<String> = Thread.currentThread().stackTrace
            .takeWhile { !it.className.contains("sun.reflect") }
            .map { stackTraceElement -> stackTraceElement.toString() }

    actual fun printLog(level: Level, msg: MESSAGE) {
        val printer = if (level >= Level.ERROR) System.err else System.out
        printer.println("[$level] $KOIN_TAG $msg")
    }
}

internal actual fun Any.ensureNeverFrozen() {}

internal actual fun <T> T.freeze(): T = this
internal actual inline fun <R> mpsynchronized(lock: Any, block: () -> R): R = synchronized(lock) {
    block()
}