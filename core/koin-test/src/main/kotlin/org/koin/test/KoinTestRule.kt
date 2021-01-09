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
package org.koin.test

import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * [TestRule] which will automatically start and stop Koin.
 *
 * @author Nick Cipollo
 * @author Jan Mottl
 */
class KoinTestRule private constructor(private val appDeclaration: KoinAppDeclaration) : TestWatcher() {

    private var _koin: Koin? = null
    val koin: Koin
        get() = _koin ?: error("No Koin application found")

    override fun starting(description: Description?) {
        _koin = startKoin(appDeclaration = appDeclaration).koin
    }

    override fun finished(description: Description?) {
        stopKoin()
        _koin = null
    }

    companion object {
        fun create(appDeclaration: KoinAppDeclaration = {}): KoinTestRule {
            return KoinTestRule(appDeclaration)
        }
    }
}
