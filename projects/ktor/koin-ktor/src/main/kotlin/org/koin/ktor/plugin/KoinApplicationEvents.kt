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
package org.koin.ktor.plugin

import io.ktor.events.*
import org.koin.core.KoinApplication

/**
 * @author Arnaud Giuliani
 * @author Victor Alenkov
 */

/**
 * Event definition for [KoinApplication] Started event
 */
val KoinApplicationStarted = EventDefinition<KoinApplication>()

/**
 * Event definition for an event that is fired when the [KoinApplication] is going to stop
 */
val KoinApplicationStopPreparing = EventDefinition<KoinApplication>()

/**
 * Event definition for [KoinApplication] Stopping event
 */
val KoinApplicationStopped = EventDefinition<KoinApplication>()
