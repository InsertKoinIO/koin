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
package org.koin.androidx.workmanager.koin

import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import androidx.work.WorkManager
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.KoinApplication

/**
 * Setup the KoinWorkerFactory instance
 *
 * @author Arnaud Giuliani
 */
fun KoinApplication.workManagerFactory() {
    createWorkManagerFactory()
}

private fun KoinApplication.createWorkManagerFactory() {
    val factory = DelegatingWorkerFactory()
        .apply {
            addFactory(KoinWorkerFactory())
        }

    val conf = Configuration.Builder()
        .setWorkerFactory(factory)
        .build()

    WorkManager.initialize(koin.get(), conf)
}