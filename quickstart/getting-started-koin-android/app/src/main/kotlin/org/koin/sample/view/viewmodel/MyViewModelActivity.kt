package org.koin.sample.view.viewmodel

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.ekito.myweatherapp.R
import kotlinx.android.synthetic.main.activity_simple.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.sample.view.java.JavaActivity

class MyViewModelActivity : AppCompatActivity() {

    // Lazy Inject ViewModel
    val myViewModel: MyViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

        title = "MyViewModelActivity"
        text.text = myViewModel.sayHello()

        background.setOnClickListener { _ ->
            startActivity(Intent(this, JavaActivity::class.java))
        }
    }
}
