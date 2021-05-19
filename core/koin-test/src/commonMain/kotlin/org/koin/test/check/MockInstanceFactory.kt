package org.koin.test.check

import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceContext
import org.koin.core.instance.InstanceFactory
import org.koin.core.scope.Scope
import org.koin.test.mock.MockProvider

class MockInstanceFactory<T>(beanDefinition: BeanDefinition<T>) :
    InstanceFactory<T>(beanDefinition) {

    override fun isCreated(context: InstanceContext?): Boolean = false

    override fun drop(scope: Scope?) {}

    override fun dropAll() {}

    override fun get(context: InstanceContext): T {
        context.koin.logger.debug("|- Instance Mock -> ${beanDefinition.primaryType}")
        return MockProvider.makeMock(beanDefinition.primaryType) as T
    }
}