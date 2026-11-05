package org.koin.sample

import androidx.lifecycle.ViewModel
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import org.koin.plugin.module.dsl.viewModel

internal class ProfileViewModel(private val users: UserRepository) : ViewModel()

// The fakes stand in for the Retrofit and auth definitions, which the page leaves out.
val profileModule = module {
    viewModel<ProfileViewModel>()
    single<ApiService> { FakeApiService() }
    single<AuthService> { FakeAuthService() }
}

fun resolveProfileViewModel(): ViewModel = KoinPlatform.getKoin().get<ProfileViewModel>()
