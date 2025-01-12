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
package org.koin.test.mock

import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.test.KoinTest
import kotlin.reflect.KClass

/**
 * Declare & Stub a mock in Koin container for given type
 *
 * @author Christian Schmitz
 */
inline fun <reified T : Any> KoinTest.declareMock(
    qualifier: Qualifier? = null,
    secondaryTypes: List<KClass<*>> = emptyList(),
    crossinline stubbing: StubFunction<T> = {},
): T {
    return getKoin().declareMock(qualifier, secondaryTypes, stubbing)
}

/**
 * Declare & Stub a mock in Koin container for given type
 *
 * @author Christian Schmitz
 */
@OptIn(KoinInternalApi::class)
inline fun <reified T : Any> Koin.declareMock(
    qualifier: Qualifier? = null,
    secondaryTypes: List<KClass<*>> = emptyList(),
    crossinline stubbing: StubFunction<T> = {},
): T {
    logger.debug("declareMock - class:${T::class} q:$qualifier")
    return scopeRegistry.rootScope.declareMock(qualifier, secondaryTypes, stubbing)
}

/**
 * Declare & Stub a mock in Koin container for given type and scope
 *
 * @author Christian Schmitz
 */
inline fun <reified T : Any> Scope.declareMock(
    qualifier: Qualifier? = null,
    secondaryTypes: List<KClass<*>> = emptyList(),
    stubbing: StubFunction<T> = {},
): T {
    val mock = MockProvider.makeMock<T>()
    declare(mock, qualifier, secondaryTypes + T::class, true, holdInstance = true)
    mock.apply(stubbing)
    return mock
}

typealias StubFunction<T> = T.() -> Unit
