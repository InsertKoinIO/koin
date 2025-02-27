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
package org.koin.dsl

import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import org.koin.core.module.KoinApplicationDslMarker

//TODO Koin 4.1 - KoinAppDeclaration migration type to KoinConfiguration

/**
 * Koin Configuration class holder, intended to replace KoinAppDeclaration that is a typealias on extension function
 *
 * use the koinConfiguration() function to define Koin configuration:
 * koinConfiguration {
 *  modules(...)
 * }
 *
 */
@KoinApplicationDslMarker
class KoinConfiguration(val config: KoinApplication.() -> Unit) {

    /**
     * return current KoinConfiguration as KoinAppDeclaration
     */
    operator fun invoke(): KoinApplication.() -> Unit {
        return config
    }

    /**
     * value exporting KoinConfiguration as KoinAppDeclaration
     * @see invoke()
     */
    val appDeclaration : KoinApplication.() -> Unit = invoke()
}

/**
 * function helper to save a Koin configuration
 *
 * @param koinConfiguration - Koin configuration lambda
 * @author Arnaud Giuliani
 */
@KoinApplicationDslMarker
public fun koinConfiguration(declaration: KoinAppDeclaration): KoinConfiguration =
    KoinConfiguration(declaration)

/**
 * Includes other KoinConfiguration in the current KoinApplication
 *
 * @param configurations - Koin configurations
 * @author Arnaud Giuliani
 */
public fun KoinApplication.includes(vararg configurations: KoinAppDeclaration?): KoinApplication {
    configurations.forEach { it?.invoke(this@includes) }
    return this
}

/**
 * Includes other KoinConfiguration in the current KoinApplication
 *
 * @param configurations - Koin configurations
 * @author Arnaud Giuliani
 */
public fun KoinApplication.includes(vararg configurations: KoinConfiguration?): KoinApplication {
    configurations.forEach { it?.config?.invoke(this@includes) }
    return this
}