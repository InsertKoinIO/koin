package com.example.app

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

// What the start page leaves out. PlatformModule has its own page.
@Singleton
class UserRepository

@KoinViewModel
class UserViewModel(val repository: UserRepository) : ViewModel()

@Module
expect class PlatformModule
