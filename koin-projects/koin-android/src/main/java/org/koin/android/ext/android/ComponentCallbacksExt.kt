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
package org.koin.android.ext.android

import android.content.ComponentCallbacks
import android.content.Context
import org.koin.android.ext.koin.bindAndroidProperties
import org.koin.android.ext.koin.context
import org.koin.android.ext.koin.with
import org.koin.android.logger.AndroidLogger
import org.koin.core.Koin
import org.koin.core.KoinContext
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.emptyParameterDefinition
import org.koin.core.scope.Scope
import org.koin.dsl.module.Module
import org.koin.log.Logger
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.createEagerInstances
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.StandAloneContext.loadProperties

/**
 * ComponentCallbacks extensions for Android
 *
 * @author Arnaud Giuliani
 */

/**
 * Create a new Koin ModuleDefinition
 * @param context - Android context
 * @param modules - list of AndroidModule
 * @param extraProperties - extra extraProperties
 * @param loadProperties - laod extraProperties from asset/koin.extraProperties
 * @param logger - default Koin logger
 *
 * will be soon deprecated for starKoin() with <context>
 */
fun ComponentCallbacks.startKoin(
    context: Context,
    modules: List<Module>,
    extraProperties: Map<String, Any> = HashMap(),
    loadProperties: Boolean = false,
    logger: Logger = AndroidLogger()
) {
    Koin.logger = logger

    val koin = loadKoinModules(modules).with(context)

    if (loadProperties || extraProperties.isNotEmpty()) {
        loadProperties(false, loadProperties, extraProperties)
        koin.bindAndroidProperties(context)
    }

    createEagerInstances(emptyParameterDefinition())
}

/**
 * inject lazily given dependency for Android component
 * @param name - bean name / optional
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T: Any> ComponentCallbacks.inject(
    name: String = "",
    scope: Scope? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = lazy { (StandAloneContext.koinContext as KoinContext).get<T>(name, scope, parameters) }

/**
 * get given dependency for Android component
 * @param name - bean name
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T: Any> ComponentCallbacks.get(
    name: String = "",
    scope: Scope? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
): T = (StandAloneContext.koinContext as KoinContext).get(name, scope, parameters)

/**
 * lazy inject given property for Android component
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> ComponentCallbacks.property(key: String): Lazy<T> =
    lazy { (StandAloneContext.koinContext as KoinContext).getProperty<T>(key) }

/**
 * lazy inject  given property for Android component
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> ComponentCallbacks.property(key: String, defaultValue: T): Lazy<T> =
    lazy { (StandAloneContext.koinContext as KoinContext).getProperty(key, defaultValue) }

/**
 * Set a property
 *
 * @param key
 * @param value
 */
fun ComponentCallbacks.setProperty(key: String, value: Any): Unit =
    context().setProperty(key, value)


/**
 * Get Koin context
 */
fun ComponentCallbacks.getKoin(): KoinContext = (org.koin.standalone.StandAloneContext.koinContext as KoinContext)

/**
 * Release a Module from given Path
 * @param path
 */
@Deprecated("release() deprecated! Please use Scope API.")
fun ComponentCallbacks.release(path: String): Unit = error("release() deprecated! Please use Scope API.")


/**
 * Release a Module from given Path
 * @param path
 */
@Deprecated("release() deprecated! Please use Scope API.")
fun ComponentCallbacks.releaseContext(path: String): Unit = error("release() deprecated! Please use Scope API.")