package org.koin.sample.androidx.scope

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.scope_activity.*
import org.koin.androidx.scope.currentScope
import org.koin.core.parameter.parametersOf
import org.koin.sample.android.R
import org.koin.sample.androidx.components.objectscope.OtherService
import org.koin.sample.androidx.components.objectscope.SomeService
import org.koin.sample.androidx.sdk.HostActivity
import org.koin.sample.androidx.utils.navigateTo

class PassingScopeActivity: AppCompatActivity() {

    val someService: SomeService by lazy { currentScope.get<SomeService>(parameters = {
        parametersOf(this)
    })}
    val otherService: OtherService by lazy { currentScope.get<OtherService>(parameters = {
        parametersOf(this)
    })}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
                .replace(R.id.scope_frame, PassingScopeFragment())
                .commit()

        setContentView(R.layout.scope_activity)

        scope_button.setOnClickListener { navigateTo<HostActivity>() }
    }
}