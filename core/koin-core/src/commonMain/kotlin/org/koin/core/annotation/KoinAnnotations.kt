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
package org.koin.core.annotation

/**
 * KoinInternal
 *
 * Help limit internal access
 */
@RequiresOptIn(message = "Used to extend current API with Koin API. Shouldn't be used outside of Koin API", level = RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class KoinInternalApi

/**
 * API marked with this annotation is "experimental", as it can still change a bit and is not guaranteed to be completely stable.
 *
 * @author Arnaud Giuliani
 * @author Victor Alenkov
 */
@RequiresOptIn(message = "This API is experimental and can change in the next versions", level = RequiresOptIn.Level.WARNING)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
)
annotation class KoinExperimentalAPI

/**
 * API marked with this annotation is using reflection
 *
 * @author Arnaud Giuliani
 * @author Victor Alenkov
 */
@RequiresOptIn(message = "This API is using reflection and implies some introspection performance penalties on limited capacity devices (Android)", level = RequiresOptIn.Level.WARNING)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
)
@Deprecated("Koin Reflection API is deprecated")
annotation class KoinReflectAPI
