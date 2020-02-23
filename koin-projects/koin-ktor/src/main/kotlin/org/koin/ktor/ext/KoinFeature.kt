/*
 * Copyright 2017-2019 the original author or authors.
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
package org.koin.ktor.ext

import io.ktor.application.Application
import io.ktor.application.ApplicationFeature
import io.ktor.application.ApplicationStopping
import io.ktor.util.AttributeKey
import org.koin.core.KoinApplication
import org.koin.core.context.KoinContextHandler
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * @author Vinicius Carvalho
 *
 * Ktor Feature class. Allows Koin Context to start using Ktor default install(<feature>) method.
 *
 */
class Koin {

    companion object Feature : ApplicationFeature<Application, KoinApplication, Koin> {
        override val key: AttributeKey<Koin>
            get() = AttributeKey("Koin")

        override fun install(pipeline: Application, configure: KoinAppDeclaration): Koin {
            startKoin(appDeclaration = configure)
            pipeline.environment.monitor.subscribe(ApplicationStopping) {
                KoinContextHandler.stop()
            }
            return Koin()
        }
    }
}
