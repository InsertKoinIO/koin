package org.koin.mp

import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import org.koin.core.state.assertMainThread
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.reflect.KClass

actual object PlatformTools {
    actual fun className(kClass: KClass<*>): String {
        return kClass.qualifiedName ?: "KClass@${hashCode()}"
    }

    actual fun printStackTrace(throwable: Throwable) {
        throwable.printStackTrace()
    }

    actual fun stackTrace(): List<String> = Exception().getStackTrace().toList()

    actual fun printLog(level: Level, msg: MESSAGE) {
        println("[$level] $KOIN_TAG $msg")
    }
}

internal actual fun Any.ensureNeverFrozen() = ensureNeverFrozen()

internal actual fun <T> T.freeze(): T = this.freeze()
internal actual inline fun <R> mpsynchronized(lock: Any, block: () -> R): R {
    assertMainThread()
    return block()
}