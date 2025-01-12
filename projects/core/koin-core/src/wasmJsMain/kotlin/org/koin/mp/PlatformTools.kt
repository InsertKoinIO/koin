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

import co.touchlab.stately.collections.ConcurrentMutableMap
import co.touchlab.stately.collections.ConcurrentMutableSet
import org.koin.core.context.GlobalContext
import org.koin.core.context.KoinContext
import org.koin.core.logger.*
import kotlin.reflect.KClass


actual object KoinPlatformTools {
    actual fun getStackTrace(e: Exception): String = e.toString() + Exception().toString().split("\n")
    actual fun getClassName(kClass: KClass<*>): String = kClass.simpleName ?: getKClassDefaultName(kClass)
    actual fun getClassFullNameOrNull(kClass: KClass<*>): String? = kClass.simpleName
    actual fun defaultLazyMode(): LazyThreadSafetyMode = LazyThreadSafetyMode.NONE
    actual fun defaultLogger(level: Level): Logger = PrintLogger(level)
    actual fun defaultContext(): KoinContext = GlobalContext
    actual fun <R> synchronized(lock: Lockable, block: () -> R) = block()
    actual fun <K, V> safeHashMap(): MutableMap<K, V> = ConcurrentMutableMap()
    actual fun <K> safeSet(): MutableSet<K> = ConcurrentMutableSet()
}