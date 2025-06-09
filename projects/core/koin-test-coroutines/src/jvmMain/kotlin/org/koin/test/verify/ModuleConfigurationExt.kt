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
package org.koin.test.verify

import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.ModuleConfiguration
import kotlin.reflect.KClass

/**
 * Verify a ModuleConfiguration, by running verification on all modules & lazy modules
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@KoinExperimentalAPI
fun ModuleConfiguration.verify(extraTypes: List<KClass<*>> = emptyList(), injections: List<ParameterTypeInjection>? = emptyList()){
    println("Verifying Module Configuration - Modules '$this' ...")
    // Verify list of modules
    val modulesVerification : Verification = _modules.map { Verification(it, extraTypes, injections) }.fold(Verification(extraTypes = extraTypes, injections = injections)) { r,v -> r+v }
    modulesVerification.verify()
    println("Verifying Module Configuration - Lazy Modules '$this' ...")
    // Verify list of lazy modules
    val lazyModulesVerification : Verification =_lazyModules.map { Verification(it.value, extraTypes, injections) }.fold(modulesVerification) { r,v -> r+v }
    lazyModulesVerification.verify()
}