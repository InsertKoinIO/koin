package org.koin.sample.androidx.scope

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.objectscope_activity.*
import org.koin.androidx.scope.currentScopeInject
import org.koin.sample.android.R
import org.koin.sample.androidx.components.objectscope.OtherService
import org.koin.sample.androidx.components.objectscope.SomeService
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