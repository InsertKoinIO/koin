package org.koin.core.bean

import org.koin.core.instance.FactoryInstance
import org.koin.core.instance.Instance
import org.koin.core.instance.ScopeInstance
import org.koin.core.instance.SingleInstance
import org.koin.core.parameter.ParametersHolder
import org.koin.core.scope.setScopeId
import kotlin.reflect.KClass

data class BeanDefinition<T>(
    val name: String? = null,
    val primaryType: KClass<*>
) {
    val secondaryTypes = arrayListOf<KClass<*>>()
    lateinit var instance: Instance<T>
    lateinit var definition: Definition<T>
    val options = Options()
    val attributes = Attributes()
    lateinit var kind: Kind

    /**
     * Tells if the definition is this Kind
     */
    fun isKind(kind: Kind): Boolean = this.kind == kind

    //TODO Log & ToString()

    companion object {
        inline fun <reified T> createSingle(
            name: String? = null,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            val beanDefinition = createDefinition(name, definition)
            beanDefinition.instance = SingleInstance(beanDefinition)
            return beanDefinition
        }

        inline fun <reified T> createScope(
            name: String? = null,
            scopeId: String,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            val beanDefinition = createDefinition(name, definition, Kind.SCOPE)
            beanDefinition.instance = ScopeInstance(beanDefinition)
            beanDefinition.setScopeId(scopeId)
            return beanDefinition
        }

        inline fun <reified T> createFactory(
            name: String? = null,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            val beanDefinition = createDefinition(name, definition, Kind.FACTORY)
            beanDefinition.instance = FactoryInstance(beanDefinition)
            return beanDefinition
        }

        inline fun <reified T> createDefinition(
            name: String?,
            noinline definition: Definition<T>,
            kind: Kind = Kind.SINGLE
        ): BeanDefinition<T> {
            val beanDefinition = BeanDefinition<T>(name, T::class)
            beanDefinition.definition = definition
            beanDefinition.kind = kind
            return beanDefinition
        }
    }
}

enum class Kind {
    SINGLE, FACTORY, SCOPE
}

typealias Definition<T> = (ParametersHolder) -> T