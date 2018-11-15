/*
 * Copyright 2017-2018 the original author or authors.
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
package org.koin.core.standalone

import org.koin.core.KoinApplication

/**
 * StandAlone Koin Application
 * Support to help inject automatically instances once KoinApp has been started
 *
 */
object StandAloneKoinApplication {
    internal var app: KoinApplication? = null

    /**
     * StandAlone Koin App instance
     */
    fun get(): KoinApplication = app ?: error("KoinApplication has not been started")

    /**
     * StandAlone Koin App instance
     */
    fun getOrNull(): KoinApplication? = app
}