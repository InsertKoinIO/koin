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
package org.koin.core.module

import org.koin.KoinCoreTest
import org.koin.Simple
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModuleIsLoadedTest : KoinCoreTest() {

    @Test
    fun `non empty module is not loaded before registration`() {
        val module = module {
            single { Simple.ComponentA() }
        }

        assertFalse(module.isLoaded)
    }

    @Test
    fun `non empty module is loaded once registered in koin application`() {
        val module = module {
            single { Simple.ComponentA() }
        }
        val app = koinApplication {
            modules(module)
        }

        assertTrue(module.isLoaded)

        app.close()
    }

    @Test
    fun `empty module is not loaded before registration`() {
        val module = module { }

        assertFalse(module.isLoaded)
    }

    @Test
    fun `empty module is loaded once registered`() {
        val module = module { }
        val app = koinApplication {
            modules(module)
        }

        assertTrue(module.isLoaded)

        app.close()
    }

    @Test
    fun `module is loaded with loadKoinModules`() {
        val module = module {
            single { Simple.ComponentA() }
        }
        assertFalse(module.isLoaded)

        startKoin { }
        loadKoinModules(module)

        assertTrue(module.isLoaded)
    }

    @Test
    fun `included module is loaded when parent module is loaded`() {
        val included = module {
            single { Simple.ComponentA() }
        }
        val parent = module {
            includes(included)
        }
        assertFalse(parent.isLoaded)
        assertFalse(included.isLoaded)

        val app = koinApplication {
            modules(parent)
        }

        assertTrue(parent.isLoaded)
        assertTrue(included.isLoaded)

        app.close()
    }
}
