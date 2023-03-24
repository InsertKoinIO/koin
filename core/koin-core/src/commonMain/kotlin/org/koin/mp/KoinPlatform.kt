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
package org.koin.mp

import org.koin.core.Koin
import org.koin.core.logger.Level
import org.koin.core.module.Module

/**
 * Koin Infrastructure API Helper
 *
 * Help to use KoinPlatformTools API and default context function in a more simple way, especially for Kotlin Multiplatform
 *
 * @author Arnaud Giuliani
 */
object KoinPlatform {

    /**
     * Start Koin with modules and logger level
     *
     * @param modules
     * @param level
     */
    fun startKoin(modules : List<Module>, level: Level){
        org.koin.core.context.startKoin {
            logger(KoinPlatformTools.defaultLogger(level))
            modules(modules)
        }
    }

    /**
     * Stop Current Koin instance
     */
    fun stopKoin(){
        org.koin.core.context.stopKoin()
    }

    /**
     * Retrieve Koin default instance
     */
    fun getKoin(): Koin = KoinPlatformTools.defaultContext().get()
}