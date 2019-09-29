package org.koin.core.definition

import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.DefaultScope
import org.koin.core.scope.ObjectScope
import org.koin.core.scope.RootScope
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

interface DefinitionFactory<S: Scope> {

    val singleScopeKind: Kind

    fun <T> createScopedWithType(
            qualifier: Qualifier? = null,
            scopeName: Qualifier? = null,
            clazz: KClass<*>,
            definition: Definition<S, T>
    ) = createDefinition(qualifier, definition, singleScopeKind, scopeName, clazz)

    fun <T> createFactoryWithType(
            qualifier: Qualifier? = null,
            scopeName: Qualifier? = null,
            clazz: KClass<*>,
            definition: Definition<S, T>
    ): BeanDefinition<S, T> {
        return createDefinition(qualifier, definition, Kind.Factory, scopeName, clazz)
    }
}

object RootScopeDefinitionFactory: DefinitionFactory<RootScope> {
    override val singleScopeKind: Kind
        get() = Kind.Single
}

object ObjectScopeDefinitionFactory: DefinitionFactory<ObjectScope<*>> {
    override val singleScopeKind: Kind
        get() = Kind.Scoped
}

object DefaultScopeDefinitionFactory: DefinitionFactory<DefaultScope> {
    override val singleScopeKind: Kind
        get() = Kind.Scoped
}

inline fun <reified S: Scope> definitionFactory(): DefinitionFactory<S> {
    return definitionFactory(S::class)
}

fun <S: Scope> definitionFactory(clazz: KClass<S>): DefinitionFactory<S> {
    return when(clazz) {
        DefaultScope::class -> DefaultScopeDefinitionFactory as DefinitionFactory<S>
        ObjectScope::class -> ObjectScopeDefinitionFactory as DefinitionFactory<S>
        else -> RootScopeDefinitionFactory as DefinitionFactory<S>
    }
}

inline fun <S: Scope, reified T> DefinitionFactory<S>.createScoped(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        noinline definition: Definition<S, T>
): BeanDefinition<S, T> {
    return this.createScopedWithType(qualifier, scopeName, T::class, definition)
}

inline fun <S: Scope, reified T> DefinitionFactory<S>.createFactory(
        qualifier: Qualifier? = null,
        scopeName: Qualifier? = null,
        noinline definition: Definition<S, T>
): BeanDefinition<S, T> {
    return this.createFactoryWithType(qualifier, scopeName, T::class, definition)
}


private fun <S: Scope, T> createDefinition(
        qualifier: Qualifier?,
        definition: Definition<S, T>,
        kind: Kind,
        scopeName: Qualifier?,
        clazz: KClass<*>
): BeanDefinition<S, T> {
    return BeanDefinition(qualifier, scopeName, clazz, kind, definition)
}