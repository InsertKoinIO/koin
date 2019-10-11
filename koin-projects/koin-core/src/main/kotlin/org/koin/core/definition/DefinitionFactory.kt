package org.koin.core.definition

import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.RootScope
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

object DefinitionFactory {

    inline fun <reified T> createSingle(
            qualifier: Qualifier? = null,
            scopeName: Qualifier? = null,
            noinline definition: Definition<RootScope, T>
    ): BeanDefinition<T> {
        return createDefinition(qualifier, definition, Kind.Single, scopeName)
    }

    inline fun <S: Scope, reified T> createScoped(
            qualifier: Qualifier? = null,
            scopeName: Qualifier? = null,
            noinline definition: Definition<S, T>
    ): BeanDefinition<T> {
        return createDefinition(qualifier, definition, Kind.Scoped, scopeName)
    }

    inline fun <S: Scope, reified T> createFactory(
            qualifier: Qualifier? = null,
            scopeName: Qualifier? = null,
            noinline definition: Definition<S, T>
    ): BeanDefinition<T> {
        return createDefinition(qualifier, definition, Kind.Factory, scopeName)
    }

    inline fun <S: Scope, reified T> createDefinition(
            qualifier: Qualifier?,
            noinline definition: Definition<S, T>,
            kind: Kind,
            scopeName: Qualifier?
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition<T>(qualifier, scopeName, T::class)
        beanDefinition.definition = definition as Definition<Scope, T>
        beanDefinition.kind = kind
        return beanDefinition
    }

    fun <T: Any> createDefinition(
            qualifier: Qualifier?,
            kind: Kind,
            scopeName: Qualifier?,
            clazz: KClass<T>,
            definition: Definition<Scope, T>
    ): BeanDefinition<T> {
        val beanDefinition = BeanDefinition<T>(qualifier, scopeName, clazz)
        beanDefinition.definition = definition
        beanDefinition.kind = kind
        return beanDefinition
    }
}