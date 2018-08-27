package org.koin.test.core.instance

import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.InstanceHolder
import org.koin.dsl.definition.BeanDefinition

/**
 * Sandbox Instance Factory
 * Create mock version of each dependency to help resolve all dependency graph without executing the real instance
 */
class SandboxInstanceFactory() : InstanceFactory() {

    override fun <T> create(def: BeanDefinition<T>): InstanceHolder<T> = SandboxInstanceHolder(def)

}