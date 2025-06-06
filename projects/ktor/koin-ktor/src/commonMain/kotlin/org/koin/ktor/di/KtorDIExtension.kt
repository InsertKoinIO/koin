/*
 * Copyright 2017-2023 the original author or authors.
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

//import io.ktor.server.application.Application
//import io.ktor.server.plugins.di.DependencyKey
//import io.ktor.server.plugins.di.dependencies
//import io.ktor.server.plugins.di.getOrNull
//import io.ktor.util.reflect.TypeInfo
//import org.koin.core.instance.ResolutionContext
//import org.koin.core.resolution.ResolutionExtension
//import org.koin.core.scope.Scope
//
///**
// * Ktor DI Resolver Extension to help Koin resolve Ktor DI objects
// *
// * @author Arnaud Giuliani
// */
//TODO Ktor 3.2 - @see setupKoinApplication()
//internal class KtorDIExtension(private val application : Application) : ResolutionExtension {
//    override val name: String = "ktor-di"
//    override fun resolve(scope: Scope, instanceContext: ResolutionContext): Any? {
//        val key = DependencyKey(TypeInfo(instanceContext.clazz), qualifier = instanceContext.qualifier?.value)
//        return application.dependencies.getOrNull(key)
//    }
//}