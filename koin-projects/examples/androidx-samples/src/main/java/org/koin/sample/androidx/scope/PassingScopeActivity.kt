package org.koin.sample.androidx.scope

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.scope_activity.*
import org.koin.androidx.scope.currentScope
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.sample.android.R
import org.koin.sample.androidx.di.OtherService
import org.koin.sample.androidx.di.SomeService
import org.koin.sample.androidx.mvp.MVPActivity
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

        scope_button.setOnClickListener { navigateTo<FailingScopeActivity>() }
    }
}