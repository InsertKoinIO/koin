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
package org.koin.core.module

/**
 * Koin Lazy Module - an implementation of [Lazy]<[Module]>.
 *
 * Accepts a lambda that initializes a [Module] via [lazy()][lazy]
 * using [LazyThreadSafetyMode.SYNCHRONIZED] as thread-safety [mode][LazyThreadSafetyMode].
 *
 * This ensures thread-safe lazy initialization, which is important when loading
 * modules in parallel background coroutines.
 *
 * @param moduleInitializer a lambda that will be used to initialize a [Module] lazily
 *
 * @author Chris Paleopanos
 * @author Arnaud Giuliani
 */
@KoinDslMarker
class LazyModule(moduleInitializer: () -> Module) : Lazy<Module> by lazy(LazyThreadSafetyMode.SYNCHRONIZED, moduleInitializer) {

    /**
     * Adds and returns [this][LazyModule] and [other] as a list of [Lazy]<[Module]>
     *
     * @param other the [LazyModule] to be added
     * @return a [List] of [Lazy]<[Module]>
     */
    operator fun plus(other: LazyModule): List<LazyModule> = listOf(this, other)

    /**
     * Adds and returns [this][LazyModule] and [others] as a list of [Lazy]<[Module]>
     *
     * @param others the [LazyModule] list to be added
     * @return a [List] of [Lazy]<[Module]>
     */
    operator fun plus(others: List<LazyModule>): List<LazyModule> = listOf(this) + others
}
