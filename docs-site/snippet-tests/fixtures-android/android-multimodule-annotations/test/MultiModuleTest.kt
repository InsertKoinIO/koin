package org.koin.sample

import org.junit.Test
import org.junit.runner.RunWith
import org.koin.mp.KoinPlatform
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import com.app.core.domain.UserRepository
import com.app.feature.login.LoginViewModel

// Starts the real Application: the feature, core and profile modules must resolve together.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MultiModuleTest {
    @Test
    fun theGraphResolvesAcrossModules() {
        val koin = KoinPlatform.getKoin()
        koin.get<LoginViewModel>()
        koin.get<UserRepository>()
    }
}
