package org.koin.core.scope

interface HasParentScope {
    val parentScopeId: ScopeID
}