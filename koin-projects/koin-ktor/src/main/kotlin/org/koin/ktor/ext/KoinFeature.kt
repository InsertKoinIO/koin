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

import io.ktor.application.*
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.ContextDsl
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * @author Vinicius Carvalho
 * @author Victor Alenkov
 *
 * Ktor Feature class. Allows Koin Context to start using Ktor default install(<feature>) method.
 *
 */
class Koin {

    companion object Feature : ApplicationFeature<Application, KoinApplication, Koin> {
        override val key = AttributeKey<Koin>("Koin")

        override fun install(pipeline: Application, configure: KoinAppDeclaration): Koin {
            startKoin(configure)
            pipeline.environment.monitor.subscribe(ApplicationStopping) {
                GlobalContext.stop()
            }
            return Koin()
        }
    }
}

/**
 * @author Victor Alenkov
 *
 * Gets or installs a [Koin] feature for the this [Application] and runs a [configuration] script on it
 */
@ContextDsl
fun Application.koin(configuration: KoinApplication.() -> Unit): Koin = featureOrNull(Koin)?.also {
    GlobalContext.get().apply(configuration)
} ?: run {
    install(Koin, configuration)
}
