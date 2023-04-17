package org.koin.sample.sandbox.scope

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import org.koin.sample.sandbox.R
import org.koin.sample.sandbox.components.ID
import org.koin.sample.sandbox.components.SCOPE_ID
import org.koin.sample.sandbox.components.SCOPE_SESSION
import org.koin.sample.sandbox.components.scope.Session
import org.koin.sample.sandbox.sdk.HostActivity
import org.koin.sample.sandbox.utils.navigateTo

class ScopedActivityB : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // No scoped Session definition in current scope
        // by definition, won;t open ScopedActivity as there is no scope definition

        // Check data from ScopedActivityA
        val session = getKoin().getScope(SCOPE_ID).get<Session>(named(SCOPE_SESSION))
        assert(ID == session.id)

        title = "Scope Activity B"
        setContentView(R.layout.scoped_activity_b)

        findViewById<Button>(R.id.scoped_b_button).setOnClickListener {
            //TODO Check WorkManagerActivity
//            navigateTo<WorkManagerActivity>(isRoot = true)
            navigateTo<HostActivity>(isRoot = true)
        }
    }
}