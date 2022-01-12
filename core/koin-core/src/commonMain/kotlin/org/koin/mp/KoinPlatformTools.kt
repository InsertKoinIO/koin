package org.koin.mp

import org.koin.core.context.KoinContext
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import kotlin.reflect.KClass

expect object KoinPlatformTools {
    fun getStackTrace(e: Exception): String
    fun getClassName(kClass: KClass<*>): String
    fun generateId(): String
    fun defaultLazyMode(): LazyThreadSafetyMode
    fun defaultLogger(level: Level = Level.INFO): Logger
    fun defaultContext(): KoinContext
    fun <R> synchronized(lock: Lockable, block: () -> R): R
    fun <K, V> safeHashMap(): MutableMap<K, V>
}

expect open class Lockable()