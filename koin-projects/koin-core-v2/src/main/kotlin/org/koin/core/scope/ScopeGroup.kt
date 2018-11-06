package org.koin.core.scope

import org.koin.core.bean.BeanDefinition
import org.koin.core.bean.Definition
import org.koin.core.module.Module

class ScopeGroup(val scopeId: String, val module: Module) {

    inline fun <reified T> scoped(
        name: String? = null,
        override: Boolean = false,
        noinline definition: Definition<T>
    ): BeanDefinition<T> {
        return module.scope(scopeId, name, override, definition)
    }

}

typealias ScopeGroupDefinition = ScopeGroup.() -> Unit
