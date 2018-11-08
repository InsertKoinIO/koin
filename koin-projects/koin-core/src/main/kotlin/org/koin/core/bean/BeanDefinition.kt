package org.koin.core.bean

import org.koin.core.instance.FactoryInstance
import org.koin.core.instance.Instance
import org.koin.core.instance.ScopeInstance
import org.koin.core.instance.SingleInstance
import org.koin.core.parameter.ParametersHolder
import org.koin.core.scope.setScopeId
import org.koin.ext.getFullName
import kotlin.reflect.KClass

data class BeanDefinition<T>(
    val name: String? = null,
    val primaryType: KClass<*>
) {
    var secondaryTypes = arrayListOf<KClass<*>>()
    lateinit var instance: Instance<T>
    lateinit var definition: Definition<T>
    var options = Options()
    var attributes = Attributes()
    lateinit var kind: Kind

    /**
     * Tells if the definition is this Kind
     */
    fun isKind(kind: Kind): Boolean = this.kind == kind

    fun isScoped() = isKind(Kind.Scope)

    fun createInstanceHolder() {
        this.instance = when (kind) {
            Kind.Single -> SingleInstance(this)
            Kind.Scope -> ScopeInstance(this)
            Kind.Factory -> FactoryInstance(this)
        }
    }

    override fun toString(): String {
        val defKind = kind.toString()
        val defName = name?.let { "name:'$name', " } ?: ""
        val defType = "type:'${primaryType.getFullName()}'"
        val defOtherTypes = if (secondaryTypes.isNotEmpty()) {
            val typesAsString = secondaryTypes.joinToString(",") { it.getFullName() }
            ", types:$typesAsString"
        } else ""
        return "$defKind[$defName$defType$defOtherTypes]"
    }

    companion object {
        inline fun <reified T> createSingle(
            name: String? = null,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            return createDefinition(name, definition)
        }

        inline fun <reified T> createScope(
            name: String? = null,
            scopeId: String,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            val beanDefinition = createDefinition(name, definition, Kind.Scope)
            beanDefinition.setScopeId(scopeId)
            beanDefinition.instance = ScopeInstance(beanDefinition)
            return beanDefinition
        }

        inline fun <reified T> createFactory(
            name: String? = null,
            noinline definition: Definition<T>
        ): BeanDefinition<T> {
            return createDefinition(name, definition, Kind.Factory)
        }

        inline fun <reified T> createDefinition(
            name: String?,
            noinline definition: Definition<T>,
            kind: Kind = Kind.Single
        ): BeanDefinition<T> {
            val beanDefinition = BeanDefinition<T>(name, T::class)
            beanDefinition.definition = definition
            beanDefinition.kind = kind
            beanDefinition.createInstanceHolder()
            return beanDefinition
        }
    }
}

enum class Kind {
    Single, Factory, Scope
}

typealias Definition<T> = (ParametersHolder) -> T