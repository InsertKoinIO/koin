package org.koin.sample.androidx.scope

import android.os.Bundle
import kotlinx.android.synthetic.main.scoped_activity_a.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.sample.android.R
import org.koin.sample.androidx.components.*
import org.koin.sample.androidx.components.scope.Session
import org.koin.sample.androidx.components.scope.SessionActivity
import org.koin.sample.androidx.utils.navigateTo

class ScopedActivityA : ScopeActivity(contentLayoutId = R.layout.scoped_activity_a) {

    // Inject from current scope
    val currentSession by inject<Session>()
    val currentActivitySession by inject<SessionActivity> { parametersOf(this) }

    // Don't mix Activity Android Scopes
    val longActivityScope: Scope by activityRetainedScope()
    val longSession by longActivityScope.inject<Session>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        assert(currentSession == get<Session>())
        if (SESSION_ID_VAR.isEmpty()) {
            println("Create ID from session: $SESSION_ID_VAR")
            SESSION_ID_VAR = longSession.id
        }
        assert(SESSION_ID_VAR == longSession.id)

        // Conpare different scope instances
        val scopeSession1 = getKoin().createScope(SESSION_1, named(SCOPE_ID))
        val scopeSession2 = getKoin().createScope(SESSION_2, named(SCOPE_ID))
        assert(scopeSession1.get<Session>(named(SCOPE_SESSION)) != currentSession)
        assert(
            scopeSession1.get<Session>(named(SCOPE_SESSION)) != scopeSession2.get<Session>(named(SCOPE_SESSION))
        )

        // set data in scope SCOPE_ID
        val session = getKoin().getOrCreateScope(SCOPE_ID, named(SCOPE_ID)).get<Session>(named(SCOPE_SESSION))
        session.id = ID

        title = "Scope Activity A"

        scoped_a_button.setOnClickListener {
            navigateTo<ScopedActivityB>(isRoot = true)
        }

        assert(this == currentActivitySession.activity)

        scopeSession1.close()
        scopeSession2.close()
    }

    companion object {
        var SESSION_ID_VAR = ""
    }
}