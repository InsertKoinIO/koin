package org.koin.sample.navigation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import org.koin.android.scope.bindScope
import org.koin.android.scope.getActivityScope
import java.util.*

class MainActivity : AppCompatActivity(), NavHost {

    val mainData by getActivityScope().inject<MainData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindScope(getActivityScope())
//        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        println("got $mainData")
    }

//    private fun setupNavigation() {
//        val navController = findNavController(this, R.id.my_nav_host_fragment)
//        setupActionBarWithNavController(this, navController)
//    }

    override fun getNavController(): NavController =
            Navigation.findNavController(this, R.id.my_nav_host_fragment)
}

data class MainData(val id: String = UUID.randomUUID().toString())
