/*
 * Copyright 2017-present the original author or authors.
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
package org.koin.androidx.workmanager.dsl

import androidx.work.ListenableWorker
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.bind

/**
 * Declare a worker
 *
 * @author : Fabio de Matos
 * @author Arnaud Giuliani
 **/
inline fun <reified T : ListenableWorker> Module.worker(
    qualifier: Qualifier = named<T>(),
    noinline definition: Definition<T>
): KoinDefinition<T> {
    val factory = factory(qualifier, definition)
    factory.bind(ListenableWorker::class)
    return factory
}
