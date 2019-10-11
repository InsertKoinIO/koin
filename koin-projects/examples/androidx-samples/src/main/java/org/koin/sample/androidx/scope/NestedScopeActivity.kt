package org.koin.sample.androidx.scope

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.nestedscope_activity.*
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.sample.android.R
import org.koin.sample.androidx.components.scope.ActivityViewModel
import org.koin.sample.androidx.utils.navigateTo

class NestedScopeActivity: AppCompatActivity() {

    private val viewModel: ActivityViewModel by currentScope.viewModel(owner = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        currentScope.declare(this)
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.nestedscope_frame, NestedScopeFragment())
                    .commit()
        }

        setContentView(R.layout.nestedscope_activity)
        title = "Nested Scope Activity"
        nestedscope_button.setOnClickListener { navigateTo<ScopedActivityA>() }

        viewModel.toolbarColorLiveData.observe(this, Observer { color ->
            supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
        })
    }

}