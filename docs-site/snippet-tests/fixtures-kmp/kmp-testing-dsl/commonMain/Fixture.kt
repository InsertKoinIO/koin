package com.example.app

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

// What the testing page leaves out: the code under test.
class Greeter { fun hello() = "hello" }

val appModule = module { single<Greeter>() }

interface Service { fun value(): String }
class RealService : Service { override fun value() = "real" }
val serviceModule = module { single<RealService>() }

interface UserRepository { fun users(): List<String> }

class UserViewModel(val repository: UserRepository) : ViewModel()

@Composable
fun UserScreen(viewModel: UserViewModel = koinViewModel()) {
    viewModel.repository.users().forEach { Text(it) }
}
