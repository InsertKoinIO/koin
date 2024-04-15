/*
 * Copyright 2017-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.koin.mp

import co.touchlab.stately.concurrency.ThreadLocalRef
import org.koin.core.context.GlobalContext
import org.koin.core.context.KoinContext
import org.koin.core.logger.*
import kotlin.random.Random
import kotlin.reflect.KClass

// actual object PlatformTools {
//    actual fun getClassName(kClass: KClass<*>): String {
//        return kClass.simpleName ?: "KClass@${hashCode()}"
//    }
//
//    actual fun printStackTrace(throwable: Throwable) {
//        throwable.printStackTrace()
//    }
//
//    actual fun stackTrace(): List<String> = Exception().toString().split("\n")
//
//    actual fun printLog(level: Level, msg: MESSAGE) {
//        println("[$level] $KOIN_TAG $msg")
//    }
// }
// internal actual fun Any.ensureNeverFrozen() {}
// internal actual fun <R> mpsynchronized(lock: Any, block: () -> R): R = block()

actual object KoinPlatformTools {
    actual fun getStackTrace(e: Exception): String = e.toString() + Exception().toString().split("\n")
    actual fun getClassName(kClass: KClass<*>): String = kClass.simpleName ?: "KClass@${kClass.hashCode()}"

    // TODO Better Id generation?
    actual fun generateId(): String = Random.nextDouble().hashCode().toString()
    actual fun defaultLazyMode(): LazyThreadSafetyMode = LazyThreadSafetyMode.NONE
    actual fun defaultLogger(level: Level): Logger = PrintLogger(level)
    actual fun defaultContext(): KoinContext = GlobalContext
    actual fun <R> synchronized(lock: Lockable, block: () -> R) = block()
    actual fun <K, V> safeHashMap(): MutableMap<K, V> = HashMap()
}

actual typealias Lockable = Any

actual typealias ThreadLocal<T> = ThreadLocalRef<T>