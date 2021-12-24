package org.koin.mp

import org.koin.core.context.GlobalContext
import org.koin.core.context.KoinContext
import org.koin.core.instance.InstanceFactory
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.PrintLogger
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

actual object KoinPlatformTools {
    actual fun getStackTrace(e: Exception): String = e.toString() + InstanceFactory.ERROR_SEPARATOR + e.stackTrace.takeWhile { !it.className.contains("sun.reflect") }.joinToString(InstanceFactory.ERROR_SEPARATOR)
    actual fun getClassName(kClass: KClass<*>): String = kClass.java.name
    actual fun generateId(): String = UUID.randomUUID().toString()
    actual fun defaultLazyMode(): LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED
    actual fun defaultLogger(level: Level): Logger = PrintLogger(level)
    actual fun defaultContext(): KoinContext = GlobalContext
    actual fun <R> synchronized(lock: SyncCapable, block: () -> R) = kotlin.synchronized(lock, block)
    actual fun <K, V> safeHashMap(): MutableMap<K, V> = ConcurrentHashMap<K, V>()
}

actual typealias SyncCapable = Any