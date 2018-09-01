package fr.ekito.myweatherapp.domain

import java.util.*

data class UserSession(val id : String = UUID.randomUUID().toString())