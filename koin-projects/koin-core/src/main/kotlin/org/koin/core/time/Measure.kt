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
 */package org.koin.core.time

import kotlin.time.ExperimentalTime
import kotlin.time.MonoClock

/**
 * Measure functions
 *
 * @author Arnaud Giuliani
 */

/**
 * Measure code execution
 */
@UseExperimental(ExperimentalTime::class)
fun measureDurationOnly(code: () -> Unit): Double {
    val clock = MonoClock
    val mark = clock.markNow()
    code()
    return mark.elapsedNow().inMilliseconds
}

/**
 * Measure code execution and get result
 */
@UseExperimental(ExperimentalTime::class)
fun <T> measureDuration(code: () -> T): Pair<T, Double> {
    val clock = MonoClock
    val mark = clock.markNow()
    val result = code()
    return Pair(result, mark.elapsedNow().inMilliseconds)
}