package fr.ekito.myweatherapp.domain.entity

import java.util.*

data class UserSession(val id: String = UUID.randomUUID().toString())