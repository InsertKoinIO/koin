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

import org.koin.core.definition.BeanDefinition
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

/**
 * Declared Instance in scope - Value holder to get back the value for the given scope Id
 * to avoid ScopedInstanceFactory where we need the bean definition to return a definition
 *
 * @author Arnaud Giuliani
 */
class DeclaredScopedInstance<T>(beanDefinition: BeanDefinition<T>, val scopeID : ScopeID) :
    InstanceFactory<T>(beanDefinition) {

    private var value : T? = null

    fun setValue(v : T){
        value = v
    }

    override fun get(context: ResolutionContext): T {
        return value ?: error("Scoped instance not found for ${context.scope.id} in $beanDefinition")
    }

    override fun isCreated(context: ResolutionContext?): Boolean = value != null

    override fun drop(scope: Scope?) {
        value = null
    }

    override fun dropAll() {
        value = null
    }
}