package org.koin.sample.androidx.compose.viewmodel

import androidx.lifecycle.ViewModel
import org.koin.sample.androidx.compose.data.User
import org.koin.sample.androidx.compose.data.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getUsers(): List<User> = userRepository.getUsers()
}