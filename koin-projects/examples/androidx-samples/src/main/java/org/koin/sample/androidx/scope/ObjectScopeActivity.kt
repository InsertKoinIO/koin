package org.koin.sample.androidx.scope

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.objectscope_activity.*
import org.koin.androidx.scope.currentScope
import org.koin.androidx.scope.currentScopeInject
import org.koin.core.bind
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.sample.android.R
import org.koin.sample.androidx.di.OtherService
import org.koin.sample.androidx.di.SomeService
import org.koin.sample.androidx.utils.navigateTo

class ObjectScopeActivity: AppCompatActivity() {

    val someService: SomeService by currentScopeInject()
    val otherService: OtherService by currentScopeInject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.objectscope_frame, ObjectScopeFragment())
                    .commit()
        }

        setContentView(R.layout.objectscope_activity)

        objectscope_button.setOnClickListener { navigateTo<PassingScopeActivity>() }
    }

}