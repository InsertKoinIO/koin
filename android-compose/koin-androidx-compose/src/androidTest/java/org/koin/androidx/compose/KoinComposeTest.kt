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
package org.koin.androidx.compose

import androidx.compose.material.Text
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import org.junit.Rule
import org.junit.Test
import org.koin.core.component.inject
import org.koin.dsl.module

private object TestScopeA
private object TestScopeB

class KoinComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displayTextFromKoin() {
        val module = module {
            factory { "testString" }
        }
        composeTestRule.setContent {
            Koin(appDeclaration = { modules(module) }) {
                val string = get<String>()
                Text(string)
            }
        }
        assertTextDisplayed("testString")
    }

    @Test
    fun useKoinFromClosestParent() {
        val outerModule = module {
            factory { "outerString" }
        }
        val innerModule = module {
            factory { "innerString" }
        }
        composeTestRule.setContent {
            Koin(appDeclaration = { modules(outerModule) }) {
                Koin(appDeclaration = { modules(innerModule) }) {
                    val string = get<String>()
                    Text(string)
                }
            }
        }
        assertTextDisplayed("innerString")
    }

    @Test
    fun useScopes() {
        val module = module {
            scope<TestScopeA> {
                scoped { "ScopeA" }
            }
            scope<TestScopeB> {
                scoped { "ScopeB" }
            }
        }
        composeTestRule.setContent {
            Koin(appDeclaration = { modules(module) }) {
                KoinScope(getScope = { createScope<TestScopeA>() }) {
                    val string = get<String>()
                    Text(string)
                }
                KoinScope(getScope = { createScope<TestScopeB>() }) {
                    val string = get<String>()
                    Text(string)
                }
            }
        }
        assertTextDisplayed("ScopeA", index = 0)
        assertTextDisplayed("ScopeB", index = 1)
    }

    @Test
    fun useInjectWithKoinComponent() {
        val module = module {
            factory { "testString" }
        }
        composeTestRule.setContent {
            Koin(appDeclaration = { modules(module) }) {
                getKoinComponent().run {
                    val string: String by inject()
                    Text(string)
                }
            }
        }
        assertTextDisplayed("testString")
    }

    private fun assertTextDisplayed(text: String, index: Int = 0) {
        composeTestRule.onNode(isRoot()).onChildAt(index).assertTextEquals(text)
    }
}