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
package org.koin.ktor.di

import io.ktor.server.application.Application
import io.ktor.server.plugins.di.*
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
/**
 * Koin implementation of Ktor's DependencyMap interface.
 * Allows Ktor DI to resolve dependencies from Koin modules.
 *
 * @param koin the Koin instance to resolve dependencies from
 * @author Arnaud Giuliani
 * @author Lidonis Calhau
 */
@OptIn(KoinInternalApi::class)
class KoinDependencyMap(private val koin: Koin) : DependencyMap {

    private val koinLogger = koin.logger

    override fun contains(key: DependencyKey): Boolean{
        koinLogger.debug("contains $key ?")
        return try {
            resolve(key)
            true
        } catch (_: NoDefinitionFoundException) {
            false
        }
    }

    override fun getInitializer(key: DependencyKey): DependencyInitializer{
        koinLogger.debug("getInitializer $key ?")

        return DependencyInitializer.Explicit(key) {
            resolve(key)
        }
    }

    // Here we are using Any because we do not have the type
    private fun resolve(key: DependencyKey): Any {
        koinLogger.debug("resolve $key ?")

        val clazz = key.type.type
        val qualifier = key.toQualifier()
        return koin.get(clazz, qualifier)
    }
}

private fun DependencyKey.toQualifier(): Qualifier? {
    return name?.let(::named)
}