package org.koin.sample.sandbox.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.sample.sandbox.R

class NavActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_activity)
    }
}