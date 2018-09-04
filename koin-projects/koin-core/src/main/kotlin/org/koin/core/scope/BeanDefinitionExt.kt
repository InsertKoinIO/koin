package org.koin.core.scope

import org.koin.dsl.definition.BeanDefinition

fun <T : Any> BeanDefinition<T>.setScope(id: String) {
    attributes["scope"] = id
}

fun <T : Any> BeanDefinition<T>.getScope(): String {
    return attributes["scope"] as? String ?: ""
}