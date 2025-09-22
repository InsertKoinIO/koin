/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.koin.android.dagger

import dagger.hilt.EntryPoints
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import kotlin.jvm.java
import kotlin.reflect.KClass

/**
 * Retrieve given T Module Entry Point from Dagger, to make it accessible to get a Dagger definition in Koin
 *
 * As Dagger Component example, to be injected in Koin :
 *
 * @InstallIn(SingletonComponent::class)
 * @EntryPoint
 * interface DaggerBridge {
 *     fun getUserDataRepository(): UserDataRepository
 * }
 *
 * Bridge Example in Koin Annotations:
 *
 * @Module
 * class AppModule {
 *
 *     @Single
 *     fun bridgeUserDataRepository(scope : Scope) = scope.dagger<DaggerBridge>().getUserDataRepository()
 * }
 *
 *  Bridge Example in Koin DSL:
 *
 *  val appModule = module {
 *      single { dagger<DaggerBridge>().getUserDataRepository() }
 *  }
 *
 * @param T - Reified type
 */
inline fun <reified T> Scope.dagger() : T{
    return EntryPoints.get(androidContext().applicationContext, T::class.java)
}

/**
 * Retrieve given Module Entry Point from Dagger, to make it accessible to get a Dagger definition in Koin
 *
 * @param clazz - Target class type
 * @see dagger() function doc
 */
fun <T> Scope.dagger(clazz : KClass<*>) : T{
    return EntryPoints.get(androidContext().applicationContext, clazz.java) as T
}