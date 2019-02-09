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
import org.koin.android.ext.koin.loadAndroidProperties
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
import org.koin.standalone.StandAloneContext.loadKoinModules

/**
 * ComponentCallbacks extensions for Android
 *
 * @author Arnaud Giuliani
 */

/**
 * Create a new Koin ModuleDefinition
 * @param androidContext - Android androidContext
 * @param modules - list of AndroidModule
 * @param extraProperties - extra extraProperties
 * @param loadPropertiesFromFile - laod extraProperties from assets/koin.properties
 * @param logger - default Koin logger
 *
 * will be soon deprecated for starKoin() with <androidContext>
 */
fun ComponentCallbacks.startKoin(
    androidContext: Context,
    modules: List<Module>,
    extraProperties: Map<String, Any> = HashMap(),
    loadPropertiesFromFile: Boolean = false,
    logger: Logger = AndroidLogger()
) {
    Koin.logger = logger

    val koin: Koin = loadKoinModules(modules).with(androidContext)

    koin.apply {
        if (loadPropertiesFromFile) {
            loadAndroidProperties(androidContext)
        }
        if (extraProperties.isNotEmpty()) {
            loadExtraProperties(extraProperties)
        }
        createEagerInstances(emptyParameterDefinition())
    }
}

/**
 * Get Koin context
 */
fun ComponentCallbacks.getKoin(): KoinContext = StandAloneContext.getKoin().koinContext

/**
 * inject lazily given dependency for Android component
 * @param name - bean name / optional
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> ComponentCallbacks.inject(
    name: String = "",
    scope: Scope? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
) = lazy { get<T>(name, scope, parameters) }

/**
 * get given dependency for Android component
 * @param name - bean name
 * @param scope
 * @param parameters - injection parameters
 */
inline fun <reified T : Any> ComponentCallbacks.get(
    name: String = "",
    scope: Scope? = null,
    noinline parameters: ParameterDefinition = emptyParameterDefinition()
): T = getKoin().get(name, scope, parameters)

/**
 * lazy inject given property for Android component
 * @param key - key property
 * throw MissingPropertyException if property is not found
 */
inline fun <reified T> ComponentCallbacks.property(key: String): Lazy<T> =
    lazy { getKoin().getProperty<T>(key) }

/**
 * lazy inject  given property for Android component
 * give a default value if property is missing
 *
 * @param key - key property
 * @param defaultValue - default value if property is missing
 *
 */
inline fun <reified T> ComponentCallbacks.property(key: String, defaultValue: T): Lazy<T> =
    lazy { getKoin().getProperty(key, defaultValue) }

/**
 * Set a property
 *
 * @param key
 * @param value
 */
fun ComponentCallbacks.setProperty(key: String, value: Any): Unit =
    getKoin().setProperty(key, value)

/**
 * Release a Module from given Path
 * @param path
 *
 * Deprecated - use the Scope API instead
 */
@Deprecated("release() deprecated! Please use Scope API.")
fun ComponentCallbacks.release(path: String): Unit = getKoin().release(path)


/**
 * Release a Module from given Path
 * @param path
 *
 * Deprecated - use the Scope API instead
 */
@Deprecated("release() deprecated! Please use Scope API.")
fun ComponentCallbacks.releaseContext(path: String): Unit = release(path)