/*
 * Copyright 2017-Present the original author or authors.
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
package org.koin.core.instance

import org.koin.core.scope.Scope

/**
 * Instance holder with order
 * Keep the order of instance definitions across modules
 * @author luozejiaqun
 */
internal data class OrderedInstanceFactory<T>(
    private val instanceFactory: InstanceFactory<T>,
    private val order: Int,
    private val ascending: Boolean,
) : InstanceFactory<T>(instanceFactory.beanDefinition), Comparable<OrderedInstanceFactory<*>> {

    override fun isCreated(context: ResolutionContext?): Boolean =
        instanceFactory.isCreated(context)

    override fun drop(scope: Scope?) {
        instanceFactory.drop(scope)
    }

    override fun dropAll() {
        instanceFactory.dropAll()
    }

    override fun create(context: ResolutionContext): T =
        instanceFactory.create(context)

    override fun get(context: ResolutionContext): T =
        instanceFactory.get(context)

    override fun compareTo(other: OrderedInstanceFactory<*>): Int {
        require(ascending == other.ascending) {
            "OrderedInstanceFactory can only be compared in same ascending order."
        }
        return if (ascending) {
            order.compareTo(other.order)
        } else {
            other.order.compareTo(order)
        }
    }
}

internal fun InstanceFactory<*>.keepDefinitionOrderAcrossModules(ascending: Boolean) =
    OrderedInstanceFactory(this, 0, ascending)
