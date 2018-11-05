package org.koin.dsl

import org.koin.core.module.Module
import org.koin.core.scope.ScopeGroup

fun Module.withScope(scopeId: String, scopeGroupDefinition: ScopeGroupDefinition) {
    return ScopeGroup(scopeId, this).let(scopeGroupDefinition)
}

typealias ScopeGroupDefinition = ScopeGroup.() -> Unit