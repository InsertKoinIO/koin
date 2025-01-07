package org.koin.mp

import co.touchlab.stately.collections.ConcurrentMutableMap
import co.touchlab.stately.collections.ConcurrentMutableSet
import co.touchlab.stately.concurrency.withLock
import org.koin.core.context.KoinContext
import org.koin.core.context.globalContextByMemoryModel
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.PrintLogger
import kotlin.reflect.KClass

actual object KoinPlatformTools {

    private val defaultContext: KoinContext by lazy {
        globalContextByMemoryModel()
    }

    actual fun getStackTrace(e: Exception): String = e.toString() + Exception().toString().split("\n")
    actual fun getClassName(kClass: KClass<*>): String = kClass.qualifiedName ?: getKClassDefaultName(kClass)
    actual fun getClassFullNameOrNull(kClass: KClass<*>): String? = kClass.qualifiedName
    actual fun defaultLazyMode(): LazyThreadSafetyMode = LazyThreadSafetyMode.PUBLICATION
    actual fun defaultLogger(level: Level): Logger = PrintLogger(level)
    actual fun defaultContext(): KoinContext = defaultContext
    actual fun <R> synchronized(lock: Lockable, block: () -> R): R = lock.lock.withLock(block)
    actual fun <K, V> safeHashMap(): MutableMap<K, V> = ConcurrentMutableMap()
    actual fun <K> safeSet(): MutableSet<K> = ConcurrentMutableSet()
}

