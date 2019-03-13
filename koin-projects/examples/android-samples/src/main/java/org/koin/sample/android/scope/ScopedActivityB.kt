package org.koin.sample.android.scope

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.scoped_activity_b.*
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.currentScope
import org.koin.core.error.NoScopeDefinitionFoundException
import org.koin.core.qualifier.named
import org.koin.sample.android.R
import org.koin.sample.android.components.ID
import org.koin.sample.android.components.SCOPE_ID
import org.koin.sample.android.components.SCOPE_SESSION
import org.koin.sample.android.components.scope.Session
import org.koin.sample.android.sdk.HostActivity
import org.koin.sample.android.utils.navigateTo

class ScopedActivityB : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // No scoped Session definition in current scope
            currentScope.get<Session>()
            fail()
        } catch (e: NoScopeDefinitionFoundException) {
        }

        // Check data from ScopedActivityA
        val session = getKoin().getScope(SCOPE_ID).get<Session>(named(SCOPE_SESSION))
        assertEquals(ID, session.id)

        title = "Scope Activity B"
        setContentView(R.layout.scoped_activity_b)

        scoped_b_button.setOnClickListener {
            navigateTo<HostActivity>()
        }
    }
}