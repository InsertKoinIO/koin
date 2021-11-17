package org.koin.sample.androidx.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.core.context.startKoin
import org.koin.sample.android.R

class NavActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_activity)
    }
}