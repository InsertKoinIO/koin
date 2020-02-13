package org.koin.mp

import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
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
