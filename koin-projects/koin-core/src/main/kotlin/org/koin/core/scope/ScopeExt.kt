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

import org.koin.core.bean.BeanDefinition

/**
 * Extensiosn for Beandefintion only for Scope cases
 *
 * @author Arnaud Giuliani
 */

const val ATTRIBUTE_SCOPE_ID = "scope_name"

/**
 * Set the scope name of a definition
 */
fun BeanDefinition<*>.setScopeName(name: String) {
    attributes[ATTRIBUTE_SCOPE_ID] = name
}

/**
 * Get a scope name of a definition
 */
fun BeanDefinition<*>.getScopeName(): String? {
    return attributes[ATTRIBUTE_SCOPE_ID]
}