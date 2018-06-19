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
package org.koin.spark

import org.koin.core.Koin
import org.koin.core.KoinContext
import org.koin.core.parameter.emptyParameterDefinition


/**
 * Run all Spark controllers
 *
 * @author Arnaud Giuliani
 */
fun KoinContext.runSparkControllers() {
    Koin.logger.log("** Run Spark Controllers **")
    beanRegistry.definitions
        .filter { it.types.contains(SparkController::class) }
        .forEach { def ->
            Koin.logger.log("Creating $def ...")
            instanceFactory.retrieveInstance<Any>(def, emptyParameterDefinition())
        }
}