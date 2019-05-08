package org.koin.core.scope

import org.koin.core.definition.BeanDefinition
import org.koin.core.instance.InstanceContext
import org.koin.core.instance.ScopeDefinitionInstance
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.ScopeSet

/**
 * Imternal Scope Definition
 */
data class ScopeDefinition(val qualifier: Qualifier, val definitions: HashSet<BeanDefinition<*>> = hashSetOf()) {

    internal fun release(instance: Scope) {
        definitions
                .filter { it.instance is ScopeDefinitionInstance<*> }
                .forEach { it.instance?.release(InstanceContext(scope = instance)) }
    }

    companion object {
        fun from(set: ScopeSet): ScopeDefinition {
            val scopeDefinition = ScopeDefinition(set.qualifier)
            scopeDefinition.definitions.addAll(set.definitions)
            return scopeDefinition
        }
    }
}