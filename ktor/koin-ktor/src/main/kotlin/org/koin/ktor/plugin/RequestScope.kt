package org.koin.ktor.plugin

import org.koin.core.Koin
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope

class RequestScope(private val _koin: Koin) : KoinScopeComponent {
    override fun getKoin(): Koin = _koin
    override val scope = createScope()
}