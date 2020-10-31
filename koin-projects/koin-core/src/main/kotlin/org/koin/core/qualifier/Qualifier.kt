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
package org.koin.core.qualifier

/**
 * Help qualify a component
 */
interface Qualifier {
    val value: QualifierValue
}

typealias QualifierValue = String

/**
 * Give a String qualifier
 */
fun named(name: String) = StringQualifier(name)

fun <E : Enum<E>> named(enum: Enum<E>) = enum.qualifier


fun qualifier(name: String) = StringQualifier(name)
fun <E : Enum<E>> qualifier(enum: Enum<E>) = enum.qualifier

fun _q(name: String) = StringQualifier(name)

/**
 * Give a Type based qualifier
 */
inline fun <reified T> named() = TypeQualifier(T::class)

/**
 * Give a Type based qualifier
 */
inline fun <reified T> qualifier() = TypeQualifier(T::class)

/**
 * Give a Type based qualifier
 */
inline fun <reified T> _q() = TypeQualifier(T::class)

val <E : Enum<E>> Enum<E>.qualifier
    get() : Qualifier {
        return StringQualifier(this.toString().toLowerCase())
    }