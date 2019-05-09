package org.koin.sample.android.scope

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.scoped_activity_a.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.bindScope
import org.koin.android.scope.currentScope
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.android.components.*
import org.koin.sample.android.components.scope.Session
import org.koin.sample.android.utils.navigateTo

class ScopedActivityA : AppCompatActivity() {

    // Inject from current scope
    val currentSession = currentScope.inject<Session>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assertEquals(currentSession.value, currentScope.get<Session>())

        // Conpare different scope instances
        val scopeSession1 = getKoin().createScope(SESSION_1, named(SCOPE_ID))
        val scopeSession2 = getKoin().createScope(SESSION_2, named(SCOPE_ID))
        assertNotEquals(scopeSession1.get<Session>(named(SCOPE_SESSION)), currentSession)
        assertNotEquals(scopeSession1.get<Session>(named(SCOPE_SESSION)), scopeSession2.get<Session>(named(SCOPE_SESSION)))

        // close scopes on lifecycle
        bindScope(scopeSession1)
        bindScope(scopeSession2, Lifecycle.Event.ON_STOP)

        // set data in scope SCOPE_ID
        val session = getKoin().createScope(SCOPE_ID, named(SCOPE_ID)).get<Session>(named(SCOPE_SESSION))
        session.id = ID

        title = "Scope Activity A"
        setContentView(R.layout.scoped_activity_a)

        scoped_a_button.setOnClickListener {
            navigateTo<ScopedActivityB>(isRoot = true)
        }
    }
}