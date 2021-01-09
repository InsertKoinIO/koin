/*
 * Copyright 2017-2021 the original author or authors.
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
package org.koin.test.junit5

import org.junit.jupiter.api.extension.*
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * [TestExtension] which will automatically start and stop Koin.
 * @author Marko Pukari
 */
class KoinTestExtension private constructor(private val appDeclaration: KoinAppDeclaration):
        BeforeEachCallback, AfterEachCallback, ParameterResolver {

    override fun beforeEach(context: ExtensionContext) {
        val koin = startKoin(appDeclaration = appDeclaration).koin
        context.store().put(Koin::class.simpleName, koin)
    }

    override fun afterEach(context: ExtensionContext) {
        context.store().remove(Koin::class.simpleName)
        stopKoin()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type == Koin::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        return extensionContext.store().get(Koin::class.simpleName)
    }

    private fun ExtensionContext.store(): ExtensionContext.Store {
        return this.getStore(KOIN_STORE)
    }

    companion object {
        fun create(appDeclaration: KoinAppDeclaration): KoinTestExtension {
            return KoinTestExtension(appDeclaration)
        }

        private val KOIN_STORE: ExtensionContext.Namespace  = ExtensionContext.Namespace.create("KOIN_STORE")
    }

}
