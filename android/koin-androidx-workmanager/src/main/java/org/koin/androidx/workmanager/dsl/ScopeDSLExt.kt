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
package org.koin.androidx.workmanager.dsl

import androidx.work.ListenableWorker
import org.koin.core.annotation.KoinReflectAPI
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.newInstance
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.ScopeDSL
import org.koin.dsl.bind

/**
 * Declare a scoped worker
 *
 * @author Arnaud Giuliani
 **/
inline fun <reified T : ListenableWorker> ScopeDSL.worker(
    qualifier: Qualifier = named<T>(),
    noinline definition: Definition<T>
): Pair<Module, InstanceFactory<*>> {
    return factory(qualifier, definition).bind<ListenableWorker>()
}

@KoinReflectAPI
inline fun <reified T : ListenableWorker> ScopeDSL.worker(
    qualifier: Qualifier = named<T>()
): Pair<Module, InstanceFactory<*>> {
    return factory(qualifier) { newInstance<T>(it) }.bind<ListenableWorker>()
}
