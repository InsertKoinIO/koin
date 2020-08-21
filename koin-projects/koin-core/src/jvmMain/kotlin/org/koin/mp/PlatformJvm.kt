package org.koin.mp

import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import org.koin.core.state.jvmThreading
import kotlin.reflect.KClass

actual object PlatformTools {
    actual fun className(kClass: KClass<*>): String = kClass.java.name
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

internal actual fun <R> mpsynchronized(lock: Any, block: () -> R): R = jvmThreading.mainOrSynchronized(lock, block)