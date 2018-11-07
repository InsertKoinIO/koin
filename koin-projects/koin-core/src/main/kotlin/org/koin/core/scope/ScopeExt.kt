package org.koin.core.scope

import org.koin.core.bean.BeanDefinition

const val ATTRIBUTE_SCOPE_ID = "scope_id"

fun BeanDefinition<*>.setScopeId(scopeId: String) {
    attributes.set(ATTRIBUTE_SCOPE_ID, scopeId)
}

fun BeanDefinition<*>.getScopeId(): String? {
    return attributes.get(ATTRIBUTE_SCOPE_ID)
}