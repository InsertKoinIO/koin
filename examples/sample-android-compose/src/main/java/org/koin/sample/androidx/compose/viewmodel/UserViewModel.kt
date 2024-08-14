package org.koin.sample.androidx.compose.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.saveable
import org.koin.sample.androidx.compose.data.User
import org.koin.sample.androidx.compose.data.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getUsers(): List<User> = userRepository.getUsers()

    init {
        println("$this created '$this'")
    }
}