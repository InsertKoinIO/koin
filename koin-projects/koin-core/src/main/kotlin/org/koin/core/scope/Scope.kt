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
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ScopeInstanceHolder

/**
 * Scope - a lifetime limited persistence space to resolve instances
 * Help to bound "scope" definition instances
 *
 * @author Arnaud Giuliani
 */
class Scope(val id : String, val scopeRegistry: ScopeRegistry) {

    internal val holders = ArrayList<ScopeInstanceHolder<*>>()

    var isClosed = false
    lateinit var instanceFactory: InstanceFactory

    fun close(){
        Koin.logger.info("[Scope] closing '$id'")
        holders.forEach {
            it.release()
            instanceFactory.release(it.bean,this)
        }
        holders.clear()
        scopeRegistry.closeScope(this)

        isClosed = true
    }

    fun size() = holders.size

    fun register(holder: ScopeInstanceHolder<*>) {
        holders += holder
    }

    override fun toString(): String {
        return "Scope['$id']"
    }
}