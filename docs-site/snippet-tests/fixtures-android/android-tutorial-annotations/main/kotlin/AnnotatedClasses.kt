package org.koin.sample

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Singleton

// The classes of the tutorial, with the annotations its Annotations tab shows on stubs.
interface UserRepository {
    fun findUserOrNull(name: String): User?
    fun addUsers(users: List<User>)
}

@Singleton
class UserRepositoryImpl : UserRepository {
    private val _users = arrayListOf<User>()
    override fun findUserOrNull(name: String): User? = _users.firstOrNull { it.name == name }
    override fun addUsers(users: List<User>) { _users.addAll(users) }
}

interface UserService {
    fun getUserOrNull(name: String = DEFAULT_USER.name): User?
    fun loadUsers()
    fun prepareHelloMessage(user: User?): String
}

@Singleton
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    init { loadUsers() }
    override fun getUserOrNull(name: String): User? = userRepository.findUserOrNull(name)
    override fun loadUsers() { userRepository.addUsers(DEFAULT_USERS) }
    override fun prepareHelloMessage(user: User?): String =
        user?.let { "Hello '${user.name}' (${user.email})!" } ?: "User not found"
}

@KoinViewModel
class UserViewModel(private val userService: UserService) : ViewModel() {
    fun sayHello(name: String): String =
        "[UserViewModel] " + userService.prepareHelloMessage(userService.getUserOrNull(name))
}
