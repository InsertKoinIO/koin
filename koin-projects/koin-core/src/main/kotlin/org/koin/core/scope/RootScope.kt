/*
 * Copyright 2017-2018 the original author or authors.
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

package org.koin.core.scope

import org.koin.core.Koin
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.DefinitionFactory
import org.koin.core.definition.Kind
import org.koin.core.error.NoBeanDefFoundException
import org.koin.core.instance.InstanceContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.ext.getFullName
import kotlin.reflect.KClass

/**
 * Encapsulates specific behavior which apply to the root scope.
 *
 * @author Andreas Schattney
 */
class RootScope(_koin: Koin): Scope(ScopeId, _koin) {

    companion object {
        internal const val ScopeId: ScopeID = "-Root-"
    }

    override fun findDefinition(qualifier: Qualifier?, clazz: KClass<*>): BeanDefinition<*>? {
        return super.findDefinition(qualifier, clazz) ?: throw noBeanDefinition(clazz)
    }

    private fun noBeanDefinition(clazz: KClass<*>): Throwable =
        NoBeanDefFoundException("No definition for '${clazz.getFullName()}' has been found. Check your module definitions.")

    override fun <T: Any> createScopedDefinition(
            qualifier: Qualifier?,
            instance: T,
            clazz: KClass<T>): BeanDefinition<T> {
        return DefinitionFactory.createDefinition(
                qualifier,
                Kind.Single,
                scopeDefinition?.qualifier,
                clazz,
                { instance }
        )
    }

    override fun <T> resolveInstance(
            qualifier: Qualifier?,
            clazz: KClass<*>,
            parameters: ParametersDefinition?): T {
        val definition = findDefinition(qualifier, clazz) ?: throw noBeanDefinition(clazz)
        return definition.resolveInstance(InstanceContext(_koin, this, parameters))
    }

    override fun createEagerInstances() {
        super.createEagerInstances()
        val definitions = beanRegistry.findAllCreatedAtStartDefinition()
        definitions.forEach {
            it.resolveInstance<Any>(InstanceContext(_koin, this))
        }
    }
}