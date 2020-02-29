package org.koin.mp

import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import kotlin.reflect.KClass

expect object PlatformTools {
    fun className(kClass: KClass<*>): String
    fun printStackTrace(throwable: Throwable)
    fun stackTrace(): List<String>
    fun printLog(level: Level, msg: MESSAGE)
}

internal fun Throwable.printStackTrace() {
    PlatformTools.printStackTrace(this)
}

internal expect fun Any.ensureNeverFrozen()

internal expect fun <R> mpsynchronized(lock: Any, block: () -> R): R