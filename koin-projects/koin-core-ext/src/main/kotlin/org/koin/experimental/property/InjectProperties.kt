/*
 * Copyright 2017-2021 the original author or authors.
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
@file:Suppress("UNCHECKED_CAST")

package org.koin.experimental.property

import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty0

fun <T : Any> T.inject(vararg properties: KMutableProperty0<*>) {
    properties.forEach {
        val prop = it as KMutableProperty0<Any>
        val type = prop.returnType.classifier as KClass<*>
        val value = GlobalContext.get().get(type)
        prop.set(value)
    }
}

fun <T : Any> T.inject(koin: Koin, vararg properties: KMutableProperty0<*>) {
    properties.forEach {
        val prop = it as KMutableProperty0<Any>
        val type = prop.returnType.classifier as KClass<*>
        val value = koin.get(type)
        prop.set(value)
    }
}

fun <T : Any> T.inject(scope: Scope, vararg properties: KMutableProperty0<*>) {
    properties.forEach {
        val prop = it as KMutableProperty0<Any>
        val type = prop.returnType.classifier as KClass<*>
        val value = scope.get(type)
        prop.set(value)
    }
}