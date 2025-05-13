/*
 * Copyright 2017-2023 the original author or authors.
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
package org.koin.module

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.ScopeDSL
import org.koin.ktor.plugin.RequestScope

/**
 * Declare a scope<RequestScope> section, to define definitions in Request scope
 *
 * @author Arnaud Giuliani
 */
@OptIn(KoinInternalApi::class)
@KoinDslMarker
inline fun Module.requestScope(scopeSet: ScopeDSL.() -> Unit) {
    val qualifier = TypeQualifier(RequestScope::class)
    ScopeDSL(qualifier, this).apply(scopeSet)
    scopes.add(qualifier)
}