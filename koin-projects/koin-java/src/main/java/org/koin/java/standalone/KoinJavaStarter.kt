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
package org.koin.java.standalone

import org.koin.core.Koin
import org.koin.dsl.module.Module
import org.koin.log.Logger
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext


/**
 * Java helper to start Koin
 *
 * @author Arnaud Giuliani
 */
object KoinJavaStarter {

    /**
     * Koin starter function to load modules and extraProperties
     * Throw AlreadyStartedException if already started
     * @param list : Modules
     * @param useEnvironmentProperties - use environment extraProperties
     * @param useKoinPropertiesFile - use /koin.extraProperties file
     * @param extraProperties - extra extraProperties
     * @param logger - Koin logger
     * @param createEagerInstances - create eager instances (eager = true in beandefinition)
     */
    @JvmOverloads
    @JvmStatic
    fun startKoin(
        list: List<Module>,
        useEnvironmentProperties: Boolean = false,
        useKoinPropertiesFile: Boolean = true,
        extraProperties: Map<String, Any> = HashMap(),
        logger: Logger = PrintLogger(),
        createEagerInstances: Boolean = false
    ): Koin = StandAloneContext.startKoin(
        list,
        useEnvironmentProperties,
        useKoinPropertiesFile,
        extraProperties,
        logger,
        createEagerInstances
    )

}