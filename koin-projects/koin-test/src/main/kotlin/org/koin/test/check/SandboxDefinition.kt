package org.koin.test.check

import org.koin.core.definition.BeanDefinition
import kotlin.reflect.KClass

/**
 * Sandbox definition to help create SandboxInstance
 */
class SandboxDefinition<T>(
        name: String? = null,
        primaryType: KClass<*>
) : BeanDefinition<T>(name, primaryType) {

    override fun createInstanceHolder() {
        this.instance = SandboxInstance(this)
    }
}