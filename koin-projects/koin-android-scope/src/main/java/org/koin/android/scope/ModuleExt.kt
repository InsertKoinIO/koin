package org.koin.android.scope

import org.koin.core.module.Module
import org.koin.core.scope.ScopeDefinition
import org.koin.ext.getFullName

inline fun <reified T> Module.scope(scopeDefinition: ScopeDefinition.() -> Unit) {
    val scopeName = T::class.getFullName()
    val scope: ScopeDefinition = ScopeDefinition(scopeName, this).apply(scopeDefinition)
    declareScope(scope)
}