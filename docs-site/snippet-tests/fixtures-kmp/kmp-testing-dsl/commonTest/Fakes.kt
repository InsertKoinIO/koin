package com.example.app

class FakeService : Service { override fun value() = "fake" }
class FakeUserRepository(private val users: List<String>) : UserRepository { override fun users() = users }
val sampleUsers = listOf("Alice")
