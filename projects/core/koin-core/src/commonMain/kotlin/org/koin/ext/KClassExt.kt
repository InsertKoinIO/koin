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
package org.koin.ext

import org.koin.mp.KoinPlatformTools
import kotlin.reflect.KClass

/**
 * Give full class qualifier
 */
fun KClass<*>.getFullName(): String {
    return classNames[this] ?: saveCache()
}

fun KClass<*>.saveCache(): String {
    val name = KoinPlatformTools.getClassName(this)
    classNames[this] = name
    return name
}

private val classNames: MutableMap<KClass<*>, String> = KoinPlatformTools.safeHashMap()
