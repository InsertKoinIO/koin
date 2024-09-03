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
package org.koin.mp

import kotlin.time.TimeSource

@Deprecated("Use kotlin.time instead.")
object KoinPlatformTimeTools {

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Use TimeSource.Monotonic.markNow() instead.")
    fun getTimeInNanoSeconds(): Long =
        TimeSource.Monotonic.markNow().elapsedNow().inWholeNanoseconds
}