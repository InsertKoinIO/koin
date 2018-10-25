package org.koin.test.core.instance

import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.holder.InstanceHolder
import org.koin.core.scope.Scope
import org.koin.dsl.definition.BeanDefinition

/**
 * Sandbox Instance Factory
 * Create mock version of each dependency to help resolve all dependency graph without executing the real instance
 */
class SandboxInstanceFactory : InstanceFactory() {

    override fun <T> create(def: BeanDefinition<T>, scope: Scope?): InstanceHolder<T> =
        SandboxInstanceHolder(def)

}