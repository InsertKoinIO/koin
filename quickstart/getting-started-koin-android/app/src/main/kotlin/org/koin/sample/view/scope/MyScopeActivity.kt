package org.koin.sample.view.scope

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.ekito.myweatherapp.R
import kotlinx.android.synthetic.main.activity_simple.*
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope
import org.koin.sample.view.viewmodel.MyViewModelActivity

class MyScopeActivity : AppCompatActivity(), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    // lazy injected MyScopePresenter
    val presenter: MyScopePresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

        println("presenter -> $presenter")

        title = "MyScopeActivity"
        text.text = presenter.sayHello()

        background.setOnClickListener { _ ->
            startActivity(Intent(this, MyViewModelActivity::class.java))
        }
    }
}
