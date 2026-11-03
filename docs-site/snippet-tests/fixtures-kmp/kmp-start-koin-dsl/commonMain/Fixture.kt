package com.example.app

import androidx.lifecycle.ViewModel
import org.koin.core.module.Module

// What the start page leaves out. platformModule() has its own page.
class UserRepository
class UserViewModel(val repository: UserRepository) : ViewModel()

expect fun platformModule(): Module
