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

import android.content.Context
import androidx.startup.Initializer
import org.koin.core.Koin
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.startKoin

/**
 * KoinInitializer handle Initializer for Koin startup process
 *
 * @author Arnaud Giuliani
 */
@KoinExperimentalAPI
class KoinInitializer : Initializer<Koin> {

    @OptIn(KoinExperimentalAPI::class)
    override fun create(context: Context): Koin {
       return if (context is KoinStartup){
            startKoin(context.onKoinStartup()).koin
        } else error("Can't start Koin configuration on current Context. Please use KoinStartup interface to define your Koin configuration with.")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}