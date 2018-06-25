package org.koin.sample.navigation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity(), NavHost {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setupNavigation()
    }

//    private fun setupNavigation() {
//        val navController = findNavController(this, R.id.my_nav_host_fragment)
//        setupActionBarWithNavController(this, navController)
//    }

    override fun getNavController(): NavController =
        Navigation.findNavController(this, R.id.my_nav_host_fragment)
}
