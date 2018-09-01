package org.koin.core.scope

import org.koin.core.Koin
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.ScopeInstanceHolder

class Scope(val id : String, val scopeRegistry: ScopeRegistry) {

    internal val holders = ArrayList<ScopeInstanceHolder<*>>()

    var isClosed = false
    lateinit var instanceFactory: InstanceFactory

    fun close(){
        Koin.logger.debug("[Scope] closing '$id'")
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