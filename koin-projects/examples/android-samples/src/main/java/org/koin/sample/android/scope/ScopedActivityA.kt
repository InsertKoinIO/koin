package org.koin.sample.android.scope

import android.os.Bundle
import kotlinx.android.synthetic.main.scoped_activity_a.*
import org.junit.Assert.*
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.ScopeActivity
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.android.components.*
import org.koin.sample.android.components.scope.Session
import org.koin.sample.android.components.scope.SessionActivity
import org.koin.sample.android.utils.navigateTo

class ScopedActivityA : ScopeActivity() {

    // Inject from current scope
    val currentSession = inject<Session>()
    val currentActivitySession = inject<SessionActivity>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertEquals(currentSession.value, get<Session>())

        // Compare different scope instances
        val scopeSession1 = getKoin().createScope(SESSION_1, named(SCOPE_ID))
        val scopeSession2 = getKoin().createScope(SESSION_2, named(SCOPE_ID))
        assertNotEquals(scopeSession1.get<Session>(named(SCOPE_SESSION)), currentSession)
        assertNotEquals(scopeSession1.get<Session>(named(SCOPE_SESSION)),
            scopeSession2.get<Session>(named(SCOPE_SESSION)))

        // set data in scope SCOPE_ID
        val session = getKoin().createScope(SCOPE_ID, named(SCOPE_ID)).get<Session>(named(SCOPE_SESSION))
        session.id = ID

        title = "Scope Activity A"
        setContentView(R.layout.scoped_activity_a)

        scoped_a_button.setOnClickListener {
            navigateTo<ScopedActivityB>(isRoot = true)
        }

        assertTrue(this == currentActivitySession.value.activity)
    }
}