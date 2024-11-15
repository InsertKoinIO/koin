/*
 * Copyright 2017-Present the original author or authors.
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

import org.koin.core.context.KoinContext
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import kotlin.reflect.KClass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

expect object KoinPlatformTools {
    fun getStackTrace(e: Exception): String
    fun getClassName(kClass: KClass<*>): String
    fun getClassFullNameOrNull(kClass: KClass<*>): String?
    fun defaultLazyMode(): LazyThreadSafetyMode
    fun defaultLogger(level: Level = Level.INFO): Logger
    fun defaultContext(): KoinContext
    fun <R> synchronized(lock: Lockable, block: () -> R): R
    fun <K, V> safeHashMap(): MutableMap<K, V>
    fun <K> safeSet(): MutableSet<K> // safe only in JVM for now
}

fun KoinPlatformTools.getKClassDefaultName(kClass: KClass<*>) : String = "KClass@${kClass.hashCode()}"
@OptIn(ExperimentalUuidApi::class)
expect fun KoinPlatformTools.generateId() : String

