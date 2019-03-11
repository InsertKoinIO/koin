package org.koin.test.check

import org.koin.core.definition.BeanDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * Sandbox definition to help create SandboxInstance
 */
class SandboxDefinition<T>(
        qualifier: Qualifier? = null,
        primaryType: KClass<*>
) : BeanDefinition<T>(qualifier, primaryType) {

    override fun createInstanceHolder() {
        this.instance = SandboxInstance(this)
    }
}