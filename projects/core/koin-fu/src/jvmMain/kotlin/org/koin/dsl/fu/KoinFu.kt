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
package org.koin.dsl.fu

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.scope.Scope
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.isAccessible

/**
 * Koin Function DSL proposal - JVM reflect version while working on coming compiler plugin
 * Optimize, warmup & cache all reflection metadata to get as fast as possible
 *
 * @author Arnaud Giuliani
 */

private val functionsCache = mutableMapOf<KClass<*>, KFunction<*>>()

@KoinInternalApi
fun <R> buildComponent(scope: Scope, function: KFunction<R>): R {
    val parameterTypes = getParameters(function)
    val args = parameterTypes.map { (clazz, isOptional) -> if (!isOptional) scope.getWithParameters<Any>(clazz = clazz) else scope.getOrNull<Any>(clazz = clazz) }.toTypedArray()
    return function.call(*args)
}

@KoinInternalApi
fun cacheFunction(function: KFunction<*>) {
    if (function !in functionsCache.values) {
        function.isAccessible = true
        function.parameters //warmup
        functionsCache[function.returnType.classifier as KClass<*>] = function
    }
}

//TODO check if we can cache params?
private fun getParameters(constructor: KFunction<*>): List<Pair<KClass<*>, Boolean>> {
    val parameterTypes = constructor.parameters.map {
        it.type.classifier as KClass<*> to it.isOptional
    }
    return parameterTypes
}