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
package org.koin.test.mock

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockProviderRule private constructor(private val mockProvider: Provider<*>) : TestRule {

    override fun apply(base: Statement, description: Description): Statement =
            object : Statement() {
                override fun evaluate() {
                    MockProvider.register(mockProvider)
                    base.evaluate()
                }
            }

    companion object {
        fun create(mockProvider: Provider<*>): MockProviderRule {
            return MockProviderRule(mockProvider)
        }
    }

}