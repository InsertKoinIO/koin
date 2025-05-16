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
package org.koin.core.instance

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.logger.Logger
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * Instance resolution Context
 * Help support DefinitionContext & DefinitionParameters when resolving definition function
 */
class ResolutionContext(
    val logger: Logger,
    val scope: Scope,
    val clazz: KClass<*>,
    val qualifier: Qualifier? = null,
    val parameters: ParametersHolder? = null,
){
    val debugTag = "t:'${clazz.getFullName()}' - q:'$qualifier'"
    var scopeArchetype : TypeQualifier? = null

    fun newContextForScope(s : Scope) : ResolutionContext{
        val rc = ResolutionContext(
            logger,
            s,
            clazz,
            qualifier,
            parameters
        )
        rc.scopeArchetype = s.scopeArchetype
        return rc
    }
}

@KoinInternalApi
class NoClass