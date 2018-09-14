package org.koin.test.scope

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.core.scope.isVisibleToScope
import org.koin.dsl.module.module
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext
import org.koin.standalone.get
import org.koin.standalone.getKoin
import org.koin.test.AutoCloseKoinTest

class ScopeVisibilityTest : AutoCloseKoinTest() {

    interface Session
    class UserSession() : Session
    class AnonSession() : Session

    class A
    class Component(val session: Session)

    val appModule = module {
        scope<Session>("auth_session") { UserSession() }
        scope<Session>("anon_session") { AnonSession() }
    }

    val appModuleWithDSL = module {
        scope<Session>("auth_session") { UserSession() }
        scope<Session>("anon_session") { AnonSession() }
        factory { Component(get(scopeId = "auth_session")) }
    }

    @Test
    fun `bean reoslution scope detection`() {
        StandAloneContext.startKoin(listOf(module {
            scope<Session>("auth_session") { UserSession() }
            scope<Session>("anon_session") { AnonSession() }
            single { A() }
        }), logger = PrintLogger(showDebug = true))

        val koin = getKoin()
        val scope = getKoin().createScope("auth_session")

        val definitions = koin.instanceRegistry.beanRegistry.definitions
        val filter = definitions.filter { it.isVisibleToScope(scope) }
        assertEquals(2, filter.count())
    }

    @Test
    fun `bean resolution - ambiguous scope visibility`() {
        StandAloneContext.startKoin(listOf(appModule), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val authSessionScope = koin.createScope("auth_session")

        val auth_session = get<Session>(scope = authSessionScope)

        assertTrue(auth_session is UserSession)

        val anonSessionScope = koin.createScope("anon_session")

        val anon_session = get<Session>(scope = anonSessionScope)

        assertTrue(anon_session is AnonSession)

        authSessionScope.close()
    }

    @Test
    fun `DSL resolution - ambiguous scope visibility`() {
        StandAloneContext.startKoin(listOf(appModuleWithDSL), logger = PrintLogger(showDebug = true))

        val koin = getKoin()

        val authSessionScope = koin.createScope("auth_session")

        val comp = get<Component>()
        val auth_session = get<Session>(scope = authSessionScope)

        assertTrue(auth_session is UserSession)
        assertEquals(auth_session, comp.session)
    }
}