package org.koin.mp

import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import kotlin.reflect.KClass

actual object PlatformTools {
    actual fun className(kClass: KClass<*>): String {
        return kClass.simpleName ?: "KClass@${hashCode()}"
    }

    actual fun printStackTrace(throwable: Throwable) {
        throwable.printStackTrace()
    }

    actual fun stackTrace(): List<String> = Exception().toString().split("\n")

    actual fun printLog(level: Level, msg: MESSAGE) {
        println("[$level] $KOIN_TAG $msg")
    }
}

internal actual fun Any.ensureNeverFrozen() {}

internal actual fun <R> mpsynchronized(lock: Any, block: () -> R): R = block()