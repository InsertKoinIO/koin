package org.koin.sample.androidx.scope

import android.os.Bundle
import kotlinx.android.synthetic.main.scoped_activity_a.*
import org.junit.Assert.*
import org.koin.androidx.scope.ScopeActivity
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.androidx.components.*
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.components.scope.SessionActivity
import org.koin.sample.androidx.utils.navigateTo

class ScopedActivityA : ScopeActivity(contentLayoutId = R.layout.scoped_activity_a) {

    // Inject from current scope
    val currentSession by inject<Session>()
    val currentActivitySession by inject<SessionActivity> { parametersOf(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertEquals(currentSession, get<Session>())

        // Conpare different scope instances
        val scopeSession1 = getKoin().createScope(SESSION_1, named(SCOPE_ID))
        val scopeSession2 = getKoin().createScope(SESSION_2, named(SCOPE_ID))
        assertNotEquals(scopeSession1.get<Session>(named(SCOPE_SESSION)), currentSession)
        assertNotEquals(scopeSession1.get<Session>(named(SCOPE_SESSION)),
                scopeSession2.get<Session>(named(SCOPE_SESSION)))


        // set data in scope SCOPE_ID
        val session = getKoin().createScope(SCOPE_ID, named(SCOPE_ID)).get<Session>(named(SCOPE_SESSION))
        session.id = ID

        title = "Scope Activity A"

        scoped_a_button.setOnClickListener {
            navigateTo<ScopedActivityB>(isRoot = true)
        }

        assertTrue(this == currentActivitySession.activity)
    }
}