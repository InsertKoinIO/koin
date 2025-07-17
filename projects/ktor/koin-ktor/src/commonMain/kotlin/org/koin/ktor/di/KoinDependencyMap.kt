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
import org.koin.core.error.NoDefinitionFoundException
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.ktor.plugin.koin

/**
 * Full DependencyMapExtension integration for Koin with Ktor 3.2 DI
 *
 * This implementation provides seamless integration by implementing the
 * DependencyMapExtension interface, allowing Ktor DI to automatically
 * resolve dependencies from Koin when not found in Ktor's registry.
 */
class KoinDependencyMapExtension : DependencyMapExtension {

    override fun get(application: Application): DependencyMap {
        return KoinDependencyMap(application.koin())
    }
}

/**
 * Koin implementation of DependencyMap interface
 */
class KoinDependencyMap(private val koin: Koin) : DependencyMap {

    override fun contains(key: DependencyKey): Boolean =
        try {
            resolve(key)
            true
        } catch (e: NoDefinitionFoundException) {
            false
        }

    override fun getInitializer(key: DependencyKey): DependencyInitializer =
        DependencyInitializer.Explicit(key) {
            resolve(key)
        }

    // Here we are using Any because we do not have the type
    private fun resolve(key: DependencyKey): Any {
        val clazz = key.type.type
        val qualifier = key.toQualifier()
        return koin.get(clazz, qualifier)
    }
}

private fun DependencyKey.toQualifier(): Qualifier? {
    return name?.let(::named)
}