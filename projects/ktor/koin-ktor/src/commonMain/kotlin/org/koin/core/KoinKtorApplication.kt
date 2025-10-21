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
package org.koin.core

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.di.DI
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.KoinApplicationDslMarker
import org.koin.ktor.di.KoinDependencyMap
import org.koin.ktor.di.KtorDIExtension

/**
 *
 */
@OptIn(KoinInternalApi::class)
@KoinApplicationDslMarker
class KoinKtorApplication() : KoinApplication() {

    var ktorApplication : Application? = null
    var ktorBridge : KtorBridgeDSL? = null

    /**
     * Setup Bridge options for Koin & Ktor
     *
     * @ee KtorDSL
     */
    @KoinExperimentalAPI
    fun bridge(option : KtorBridgeDSL.() -> Unit){
        ktorBridge = KtorBridgeDSL()
        ktorBridge!!.option()
    }

    internal fun onPostStart(){
        ktorBridge?.let { ktorBridge ->
            if (ktorBridge.bridgeKoinToKtor){
                onBridgeKoinToKtor()
            }
            if (ktorBridge.bridgeKtorToKoin){
                onBridgeKtorToKoin()
            }
        }
    }

    internal fun onBridgeKoinToKtor(){
        koin.logger.debug("Ktor DI Bridge: Koin -> Ktor")

        koin.resolver.addResolutionExtension(KtorDIExtension(ktorApplication ?: error("KoinKtorApplication has no ktorApplication, when using koinToKtor()")))
    }

    internal fun onBridgeKtorToKoin(){
        koin.logger.debug("Ktor DI Bridge: Ktor -> Koin")

        val ktorApp = ktorApplication ?: error("KoinKtorApplication has no ktorApplication, when using ktorToKoin() ")
        ktorApp.install(DI){
            include(KoinDependencyMap(koin))
        }
    }

    companion object {

        fun init() : KoinKtorApplication {
            return KoinKtorApplication()
        }
    }
}

/**
 *
 */
@OptIn(KoinInternalApi::class)
@KoinApplicationDslMarker
class KtorBridgeDSL {

    internal var bridgeKoinToKtor : Boolean = false
        private set

    internal var bridgeKtorToKoin : Boolean = false
        private set

    @KoinExperimentalAPI
    fun koinToKtor() {
        bridgeKoinToKtor = true
    }

    @KoinExperimentalAPI
    fun ktorToKoin() {
        bridgeKtorToKoin = true
    }
}