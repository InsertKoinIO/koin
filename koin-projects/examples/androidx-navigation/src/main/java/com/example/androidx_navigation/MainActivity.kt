package com.example.androidx_navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class MainActivityScopeDep(private val appScopeDep: AppScopeDep)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadKoinModules(module {
            single { MainActivityScopeDep(get()) }
        })
    }
}
