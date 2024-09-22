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
package org.koin.dsl

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.module._scopedInstanceFactory
import org.koin.core.qualifier.Qualifier

/**
 * DSL Scope Definition
 */
@OptIn(KoinInternalApi::class)
@KoinDslMarker
class ScopeDSL(val scopeQualifier: Qualifier, val module: Module) {

    inline fun <reified T> scoped(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
    ): KoinDefinition<T> {
        val def = _scopedInstanceFactory(qualifier, definition, scopeQualifier)
        module.indexPrimaryType(def)
        return KoinDefinition(module, def)
    }

    inline fun <reified T> factory(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
    ): KoinDefinition<T> {
        return module.factory(qualifier, definition, scopeQualifier)
    }
}
