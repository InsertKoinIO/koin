package org.koin.core.scope

import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definitions
import org.koin.core.definition.Options
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier._q
import org.koin.mp.ensureNeverFrozen
import kotlin.reflect.KClass

/**
 * Internal Scope Definition
 */
class ScopeDefinition(val qualifier: Qualifier, val isRoot: Boolean = false, private val _definitions: HashSet<BeanDefinition<*>> = hashSetOf()) {

    init {
        ensureNeverFrozen()
    }

    val definitions: Set<BeanDefinition<*>>
        get() = _definitions

    fun save(beanDefinition: BeanDefinition<*>, forceOverride: Boolean = false) {
        if (definitions.contains(beanDefinition)) {
            if (beanDefinition.options.override || forceOverride) {
                _definitions.remove(beanDefinition)
            } else {
                val current = definitions.firstOrNull { it == beanDefinition }
                throw DefinitionOverrideException("Definition '$beanDefinition' try to override existing definition. Please use override option or check for definition '$current'")
            }
        }
        _definitions.add(beanDefinition)
    }

    fun remove(beanDefinition: BeanDefinition<*>) {
        _definitions.remove(beanDefinition)
    }

    internal fun size() = definitions.size

    fun <T : Any> saveNewDefinition(
            instance: T,
            qualifier: Qualifier? = null,
            secondaryTypes: List<KClass<*>>? = null,
            override: Boolean = false
    ): BeanDefinition<out Any?> {
        val clazz = instance::class
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
                clazz,
                qualifier,
                { instance },
                this,
                Options(isCreatedAtStart = false, override = override),
                secondaryTypes ?: emptyList()
        )
        save(beanDefinition, override)
        return beanDefinition
    }

    fun unloadDefinitions(scopeDefinition: ScopeDefinition) {
        scopeDefinition.definitions.forEach {
            _definitions.remove(it)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScopeDefinition) return false

        if (qualifier != other.qualifier) return false
        if (isRoot != other.isRoot) return false

        return true
    }

    override fun hashCode(): Int {
        var result = qualifier.hashCode()
        result = 31 * result + isRoot.hashCode()
        return result
    }

    fun copy(): ScopeDefinition {
        val copy = ScopeDefinition(qualifier, isRoot, HashSet())
        copy._definitions.addAll(definitions)
        return copy
    }

    companion object {
        const val ROOT_SCOPE_ID = "-Root-"
        val ROOT_SCOPE_QUALIFIER = _q(ROOT_SCOPE_ID)
        fun rootDefinition() = ScopeDefinition(ROOT_SCOPE_QUALIFIER, isRoot = true)
    }
}