/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.core.scope

import org.koin.core.Koin
import org.koin.core.instance.holder.ScopeInstanceHolder
import org.koin.dsl.definition.BeanDefinition
import org.koin.dsl.definition.Kind
import org.koin.standalone.StandAloneContext
import java.util.UUID

/**
 * Scope - a lifetime limited persistence space to resolve instances
 * Help to bound "scope" definition instances
 *
 * @author Arnaud Giuliani
 */
data class Scope(
    val id: String,
    val uuid: String = UUID.randomUUID().toString(),
    val isDetached: Boolean = false
) {
    val parameters = hashMapOf<String, Any>()

    fun close() {
        val koin = StandAloneContext.getKoin()

        removeInstanceHolders(koin)

        removeAddedDefinitions(koin)

        removeScope(koin)
    }

    private fun removeScope(koin: Koin) {
        koin.koinContext.scopeRegistry.deleteScope(id, uuid)
    }

    private fun removeAddedDefinitions(koin: Koin) {
        val addedDefinitions = koin.beanRegistry.definitions.filter {
            it.getScope() == id && it.isAddedToScope()
        }
        addedDefinitions.forEach {
            koin.beanRegistry.definitions.remove(it)
        }
    }

    private fun removeInstanceHolders(koin: Koin) {
        val scopedInstances = koin.instanceFactory.instances.filter {
            it is ScopeInstanceHolder && it.scope == this
        }
        scopedInstances.forEach { it.release() }
        koin.instanceFactory.instances.removeAll(scopedInstances)
    }

    inline fun <reified T> addInstance(instance: T) {
        if (!isDetached) {
            val definition = BeanDefinition(
                primaryType = T::class,
                definition = { instance },
                kind = Kind.Scope,
                allowOverride = true
            )
            definition.setScope(id)
            definition.setAddedToScope()
            StandAloneContext.getKoin().declare(definition)
        } else {
            error("Can't add extra instances on detached scope")
        }
    }

    override fun toString(): String {
        return "Scope['$id'-$uuid]"
    }
}