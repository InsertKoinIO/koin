/*
 * Copyright 2017-present the original author or authors.
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
package org.koin.ktor.plugin

import org.koin.core.Koin
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.mp.KoinPlatformTools
import org.koin.mp.generateId

/**
 * Request Scope Holder
 *
 * @author Arnaud Giuliani
 */
class RequestScope(private val _koin: Koin) : KoinScopeComponent {
    private val scopeId = "request_"+KoinPlatformTools.generateId()
    override fun getKoin(): Koin = _koin
    override val scope = createScope(scopeId = scopeId)
}