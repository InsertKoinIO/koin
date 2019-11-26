package org.koin.core.scope

import org.koin.core.definition.BeanDefinition
import org.koin.core.error.DefinitionOverrideException
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier._q

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

    fun size() = definitions.size

    companion object {
        const val ROOT_SCOPE_ID = "-Root-"
        val ROOT_SCOPE_QUALIFIER = _q(ROOT_SCOPE_ID)
        fun rootDefinition() = ScopeDefinition(ROOT_SCOPE_QUALIFIER, isRoot = true)
    }
}