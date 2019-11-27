package org.koin.core.scope

import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definitions
import org.koin.core.definition.Options
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier._q
import org.koin.dsl.binds
import kotlin.reflect.KClass

/**
 * Imternal Scope Definition
 */
data class ScopeDefinition(val qualifier: Qualifier, val isRoot: Boolean = false) {

    private val _definitions: HashSet<BeanDefinition<*>> = hashSetOf()
    val definitions: Set<BeanDefinition<*>>
        get() = _definitions

    fun save(beanDefinition: BeanDefinition<*>) {
        if (definitions.contains(beanDefinition)) {
            if (beanDefinition.options.override) {
                _definitions.remove(beanDefinition)
            } else {
                val current = definitions.firstOrNull { it == beanDefinition }
                throw DefinitionOverrideException("Definition '$beanDefinition' try to override existing definition. Please use override option or check for definition '$current'")
            }
        }
        _definitions.add(beanDefinition)
    }

    fun remove(beanDefinition: BeanDefinition<*>){
        _definitions.remove(beanDefinition)
    }

    internal fun size() = definitions.size

    inline fun <reified T> declare(
        instance: T,
        qualifier: Qualifier? = null,
        secondaryTypes: List<KClass<*>>? = null,
        override: Boolean = false
    ): BeanDefinition<out Any?> {
        val clazz = T::class
        val found: BeanDefinition<*>? =
            definitions.firstOrNull { def -> def.`is`(clazz, qualifier, this) }
        if (found != null) {
            if (override) {
                remove(found)
            } else {
                throw DefinitionOverrideException("Trying to override existing definition '$found' with new definition typed '$clazz'")
            }
        }
        val beanDefinition = Definitions.createSingle(
            qualifier,
            { instance },
            this,
            Options(isCreatedAtStart = false, override = override)
        )
        val finalDefinition =
            secondaryTypes?.let { beanDefinition.binds(it.toTypedArray()) } ?: beanDefinition
        save(
            finalDefinition
        )
        return finalDefinition
    }

    companion object {
        const val ROOT_SCOPE_ID = "-Root-"
        val ROOT_SCOPE_QUALIFIER = _q(ROOT_SCOPE_ID)
        fun rootDefinition() = ScopeDefinition(ROOT_SCOPE_QUALIFIER, isRoot = true)
    }
}