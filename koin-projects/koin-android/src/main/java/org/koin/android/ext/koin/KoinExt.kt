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

package org.koin.android.ext.koin

import android.content.Context
import org.koin.core.Koin
import org.koin.dsl.definition.BeanDefinition
import org.koin.dsl.path.Path
import java.util.*

/**
 * Koin extensions for Android
 *
 * @author Arnaud Giuliani
 */


infix fun Koin.with(androidContext: Context): Koin {
    Koin.logger.info("[init] declare Android Context")
    beanRegistry.declare(
        BeanDefinition(
            clazz = Context::class,
            definition = { androidContext }), Path.root()
    )
    return this
}

/**
 * Load properties file from Assets
 * @param androidContext
 * @param koinPropertyFile
 */
fun Koin.bindAndroidProperties(
    androidContext: Context,
    koinPropertyFile: String = "koin.properties"
): Koin {
    val koinProperties = Properties()
    try {
        val hasFile = androidContext.assets.list("").contains(koinPropertyFile)
        if (hasFile) {
            try {
                androidContext.assets.open(koinPropertyFile).use { koinProperties.load(it) }
                val nb = propertyResolver.import(koinProperties)
                Koin.logger.info("[Android-Properties] loaded $nb properties from assets/koin.properties")
            } catch (e: Exception) {
                Koin.logger.info("[Android-Properties] error for binding properties : $e")
            }
        } else {
            Koin.logger.info("[Android-Properties] no assets/koin.properties file to load")
        }
    } catch (e: Exception) {
        Koin.logger.err("[Android-Properties] error while loading properties from assets/koin.properties : $e")
    }
    return this
}
