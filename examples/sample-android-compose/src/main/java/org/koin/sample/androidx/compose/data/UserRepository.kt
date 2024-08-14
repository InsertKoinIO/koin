package org.koin.sample.androidx.compose.data

class UserRepository {

    fun getUsers(): List<User> {
        return listOf(
                User(name = "Peter", age = 32),
                User(name = "John", age = 27),
                User(name = "Paul", age = 18)
        )
    }
}