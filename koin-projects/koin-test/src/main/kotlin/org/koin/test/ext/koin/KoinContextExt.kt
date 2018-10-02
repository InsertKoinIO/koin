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
package org.koin.test.ext.koin

import org.koin.core.Koin
import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.dsl.definition.BeanDefinition
import kotlin.reflect.KClass


/**
 * KoinContext extensions for tests & tools
 *
 * dryrun - run modules & checkModules each instance is ok
 * checkModules - checkModules each dependency described in module
 *
 * @author Arnaud Giuliani
 */


/**
 * Check all loaded definitions by resolving them one by one
 */
fun KoinContext.dryRun(defaultParameters: ParameterDefinition) {
    Koin.logger?.info("[Check Modules]")

    instanceRegistry.dryRun(defaultParameters)
}

/**
 * Return all definitions of Koin
 */
fun KoinContext.beanDefinitions() = instanceRegistry.beanRegistry.definitions

/**
 * return beanDefinition for given class
 * @param clazz - bean class
 */
fun KoinContext.beanDefinition(clazz: KClass<*>): BeanDefinition<*>? =
    beanDefinitions().firstOrNull() { it.clazz == clazz }

/**
 * Return all contexts of Koin
 */
fun KoinContext.allPaths() = instanceRegistry.pathRegistry.paths

/**
 * Return all instances of Koin
 */
fun KoinContext.allInstances() = instanceRegistry.instanceFactory.instances.toList()

/**
 * Return all properties of Koin
 */
fun KoinContext.allProperties() = propertyResolver.properties

/**
 * return path
 * @param path
 */
fun KoinContext.getPath(path: String) = allPaths().first { it.name == path }
