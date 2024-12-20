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
package org.koin.androix.startup

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.KoinApplicationDslMarker
import org.koin.dsl.KoinConfiguration

/**
 * KoinStartup holds KoinAppDeclaration for AndroidX Startup with KoinInitializer
 *
 * @author Arnaud Giuliani
 */
@KoinExperimentalAPI
interface KoinStartup {

    /**
     * startKoin with AndroidX startup Initializer
     * @see startKoin function
     */
    @KoinApplicationDslMarker
    fun onKoinStartup() : KoinConfiguration
}