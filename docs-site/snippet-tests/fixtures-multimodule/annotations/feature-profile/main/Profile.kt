package com.app.feature.profile

import androidx.lifecycle.ViewModel
import com.app.core.domain.UserRepository
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.koin.mp.KoinPlatform

@Module
@ComponentScan("com.app.feature.profile")
@Configuration
class ProfileModule

@KoinViewModel
internal class ProfileViewModel(private val users: UserRepository) : ViewModel()

fun resolveProfileViewModel(): ViewModel = KoinPlatform.getKoin().get<ProfileViewModel>()
