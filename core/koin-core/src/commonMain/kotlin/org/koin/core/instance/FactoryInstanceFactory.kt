/*
 * Copyright 2017-2023 the original author or authors.
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

import org.koin.core.definition.BeanDefinition
import org.koin.core.scope.Scope

/**
 * Factory Instance Holder
 *
 * @author Arnaud Giuliani
 */
class FactoryInstanceFactory<T>(beanDefinition: BeanDefinition<T>) :
    InstanceFactory<T>(beanDefinition) {

    override fun isCreated(context: InstanceContext?): Boolean = false

    override fun drop(scope: Scope?) {
        beanDefinition.callbacks.onClose?.invoke(null)
    }

    override fun dropAll(){}

    override fun get(context: InstanceContext): T {
        return create(context)
    }
}