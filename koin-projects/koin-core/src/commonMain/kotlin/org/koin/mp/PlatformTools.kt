package org.koin.mp

import org.koin.core.context.KoinContext
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import kotlin.reflect.KClass

expect object PlatformTools {
    fun getStackTrace(e: Exception): String
    fun getClassName(kClass: KClass<*>): String
    fun generateId(): String
    fun defaultLogger(level: Level = Level.INFO): Logger
    fun defaultContext(): KoinContext
    fun <R> synchronized(lock: Any, block: () -> R): R
    fun <K, V> safeHashMap(): MutableMap<K, V>
}