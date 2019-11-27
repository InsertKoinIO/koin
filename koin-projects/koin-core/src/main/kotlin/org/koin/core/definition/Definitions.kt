package org.koin.core.definition

import org.koin.core.Koin
import org.koin.core.instance.FactoryInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.ScopeDefinition

object Definitions {

    inline fun <reified T> saveSingle(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
        scopeDefinition: ScopeDefinition,
        options: Options
    ): BeanDefinition<T> {
        val beanDefinition = createSingle(qualifier,definition,scopeDefinition,options)
        scopeDefinition.save(beanDefinition)
        return beanDefinition
    }

    inline fun <reified T> createSingle(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
        scopeDefinition: ScopeDefinition,
        options: Options
    ): BeanDefinition<T> {
        return BeanDefinition(
            scopeDefinition,
            T::class,
            qualifier,
            { koin: Koin, beanDef: BeanDefinition<*> -> SingleInstanceFactory(koin, beanDef) },
            definition,
            Kind.Single,
            options = options
        )
    }

    inline fun <reified T> createFactory(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
        scopeDefinition: ScopeDefinition,
        options: Options
    ): BeanDefinition<T> {
        return BeanDefinition(
            scopeDefinition,
            T::class,
            qualifier,
            { koin: Koin, beanDef: BeanDefinition<*> -> FactoryInstanceFactory(koin, beanDef) },
            definition,
            Kind.Factory,
            options = options
        )
    }

    inline fun <reified T> saveFactory(
        qualifier: Qualifier? = null,
        noinline definition: Definition<T>,
        scopeDefinition: ScopeDefinition,
        options: Options
    ): BeanDefinition<T> {
        val beanDefinition = createFactory(qualifier,definition,scopeDefinition,options)
        scopeDefinition.save(beanDefinition)
        return beanDefinition
    }
}