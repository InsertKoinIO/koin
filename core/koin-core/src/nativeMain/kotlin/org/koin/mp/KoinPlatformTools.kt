package org.koin.mp

import co.touchlab.stately.concurrency.Lock
import co.touchlab.stately.concurrency.withLock
import co.touchlab.stately.concurrency.ThreadLocalRef
import org.koin.core.context.KoinContext
import org.koin.core.context.globalContextByMemoryModel
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.PrintLogger
import kotlin.random.Random
import kotlin.reflect.KClass

actual object KoinPlatformTools {

    private val defaultContext: KoinContext by lazy {
        globalContextByMemoryModel()
    }

    actual fun getStackTrace(e: Exception): String = e.toString() + Exception().toString().split("\n")
    actual fun getClassName(kClass: KClass<*>): String = kClass.qualifiedName ?: "KClass@${kClass.hashCode()}"

    // TODO Better Id generation?
    actual fun generateId(): String = Random.nextDouble().hashCode().toString()
    actual fun defaultLazyMode(): LazyThreadSafetyMode = LazyThreadSafetyMode.PUBLICATION
    actual fun defaultLogger(level: Level): Logger = PrintLogger(level)
    actual fun defaultContext(): KoinContext = defaultContext

    actual fun <R> synchronized(lock: Lockable, block: () -> R): R = lock.lock.withLock { block() }

    actual fun <K, V> safeHashMap(): MutableMap<K, V> = HashMap()
}

actual open class Lockable {
    internal val lock = Lock()
}

actual typealias ThreadLocal<T> = ThreadLocalRef<T>